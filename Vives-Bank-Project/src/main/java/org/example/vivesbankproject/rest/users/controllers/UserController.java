package org.example.vivesbankproject.rest.users.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.vivesbankproject.rest.users.dto.UserRequest;
import org.example.vivesbankproject.rest.users.dto.UserResponse;
import org.example.vivesbankproject.rest.users.services.UserService;
import org.example.vivesbankproject.utils.pagination.PageResponse;
import org.example.vivesbankproject.utils.pagination.PaginationLinksUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
 /** @author Jaime León, Natalia González, Germán Fernández, Alba García, Mario de Domingo, Alvaro Herrero
 * @version 1.0-SNAPSHOT
 */

 @Slf4j
 @Validated
 @RestController
 @RequestMapping("${api.version}/usuarios")
 @PreAuthorize("hasRole('ADMIN')")
 public class UserController {

     private final UserService userService;
     private final PaginationLinksUtils paginationLinksUtils;

     @Autowired
     public UserController(UserService userService, PaginationLinksUtils paginationLinksUtils) {
         this.userService = userService;
         this.paginationLinksUtils = paginationLinksUtils;
     }

     /**
      * Obtiene una lista de usuarios de manera paginada con filtros opcionales por nombre de usuario y roles.
      *
      * @param username El nombre de usuario para filtrar resultados (opcional).
      * @param roles Los roles para filtrar los resultados (opcional).
      * @param page Número de la página que se desea obtener (por defecto 0).
      * @param size Cantidad de resultados por página (por defecto 10).
      * @param sortBy Campo por el que se ordenarán los resultados (por defecto "id").
      * @param direction Dirección de ordenamiento: "asc" para ascendente, "desc" para descendente (por defecto "asc").
      * @param request El objeto HttpServletRequest para construir los enlaces de paginación.
      * @return Una respuesta con los usuarios paginados y un encabezado de enlace para la paginación.
      */
     @GetMapping()
     @Operation(
             summary = "Obtener usuarios paginados",
             description = "Devuelve una lista de usuarios paginados con soporte para filtrado por nombre de usuario y roles.",
             responses = {
                     @ApiResponse(responseCode = "200", description = "Lista de usuarios devuelta con éxito."),
                     @ApiResponse(responseCode = "500", description = "Error interno al obtener los usuarios.")
             }
     )
     public ResponseEntity<PageResponse<UserResponse>> getAllPageable(
             @RequestParam(required = false) Optional<String> username,
             @RequestParam(required = false) Optional<String> roles,
             @RequestParam(defaultValue = "0") int page,
             @RequestParam(defaultValue = "10") int size,
             @RequestParam(defaultValue = "id") String sortBy,
             @RequestParam(defaultValue = "asc") String direction,
             HttpServletRequest request) {
         log.info("Buscando todos los usuarios con las siguientes opciones: {}, {}", username, roles);
         Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

         UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());
         Page<UserResponse> pageResult = userService.getAll(username, roles, PageRequest.of(page, size, sort));
         return ResponseEntity.ok()
                 .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                 .body(PageResponse.of(pageResult, sortBy, direction));
     }

     /**
      * Obtiene un usuario por su identificador único.
      *
      * @param id El identificador del usuario que se desea obtener.
      * @return La información del usuario en caso de ser encontrado.
      */
     @GetMapping("{id}")
     @Operation(
             summary = "Obtener usuario por ID",
             description = "Devuelve la información de un usuario específico basado en su identificador.",
             responses = {
                     @ApiResponse(responseCode = "200", description = "Usuario devuelto correctamente."),
                     @ApiResponse(responseCode = "404", description = "Usuario no encontrado.")
             }
     )
     public ResponseEntity<UserResponse> getById(@PathVariable String id) {
         return ResponseEntity.ok(userService.getById(id));
     }

     /**
      * Crea un nuevo usuario.
      *
      * @param userRequest Los datos del nuevo usuario que se desean guardar.
      * @return La información del usuario recién creado.
      */
     @PostMapping()
     @Operation(
             summary = "Crear un nuevo usuario",
             description = "Crea un nuevo usuario con los datos proporcionados.",
             responses = {
                     @ApiResponse(responseCode = "201", description = "Usuario creado con éxito."),
                     @ApiResponse(responseCode = "400", description = "Solicitud incorrecta por datos inválidos."),
                     @ApiResponse(responseCode = "500", description = "Error interno al crear el usuario.")
             }
     )
     public ResponseEntity<UserResponse> save(@Valid @RequestBody UserRequest userRequest) {
         return ResponseEntity.status(HttpStatus.CREATED).body(userService.save(userRequest));
     }

     /**
      * Actualiza un usuario existente.
      *
      * @param id El identificador del usuario que se desea actualizar.
      * @param userRequest Los nuevos datos para actualizar el usuario.
      * @return La información actualizada del usuario.
      */
     @PutMapping("{id}")
     @Operation(
             summary = "Actualizar un usuario",
             description = "Actualiza la información de un usuario existente.",
             responses = {
                     @ApiResponse(responseCode = "200", description = "Usuario actualizado con éxito."),
                     @ApiResponse(responseCode = "404", description = "Usuario no encontrado."),
                     @ApiResponse(responseCode = "400", description = "Solicitud incorrecta por datos inválidos."),
                     @ApiResponse(responseCode = "500", description = "Error interno al actualizar el usuario.")
             }
     )
     public ResponseEntity<UserResponse> update(@PathVariable String id, @Valid @RequestBody UserRequest userRequest) {
         return ResponseEntity.ok(userService.update(id, userRequest));
     }

     /**
      * Elimina un usuario por su identificador.
      *
      * @param id El identificador del usuario que se desea eliminar.
      * @return Respuesta con estado 204 si la eliminación fue exitosa.
      */
     @DeleteMapping("{id}")
     @Operation(
             summary = "Eliminar un usuario",
             description = "Elimina un usuario específico de la base de datos.",
             responses = {
                     @ApiResponse(responseCode = "204", description = "Usuario eliminado con éxito."),
                     @ApiResponse(responseCode = "404", description = "Usuario no encontrado."),
                     @ApiResponse(responseCode = "500", description = "Error interno al eliminar el usuario.")
             }
     )
     public ResponseEntity<Void> delete(@PathVariable String id) {
         userService.deleteById(id);
         return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
     }

     /**
      * Maneja las excepciones por validaciones que no cumplan las restricciones esperadas.
      *
      * @param ex Excepción lanzada al no cumplir las validaciones.
      * @return Un mapa con información de los errores.
      */
     @ResponseStatus(HttpStatus.BAD_REQUEST)
     @ExceptionHandler(MethodArgumentNotValidException.class)
     @Operation(
             summary = "Manejar errores de validación",
             description = "Devuelve un mapa de errores al ocurrir excepciones de validación.",
             responses = {
                     @ApiResponse(responseCode = "400", description = "Errores de validación devueltos correctamente.")
             }
     )
     public Map<String, String> handleValidationExceptions(
             MethodArgumentNotValidException ex) {
         Map<String, String> errors = new HashMap<>();
         ex.getBindingResult().getAllErrors().forEach((error) -> {
             String fieldName = ((FieldError) error).getField();
             String errorMessage = error.getDefaultMessage();
             errors.put(fieldName, errorMessage);
         });
         return errors;
     }
 }