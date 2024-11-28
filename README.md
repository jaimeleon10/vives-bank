# Descripción del programa

En esta práctica, desarrollaremos un servicio bancario donde registraremos a los clientes con sus respectivos usuarios, cuentas, tarjetas y movimientos. Implementaremos diferentes técnicas de programación para garantizar la integridad y robustez del sistema, además de aplicar los principios SOLID.


## Tecnologías y Enfoques Utilizados

* Arquitectura orientada al dominio.
* Manejo de excepciones orientadas al dominio.
* Uso de inyección de dependencias.


## Base de Datos

Este proyecto utiliza dos bases de datos para gestionar la información de usuarios y movimientos:

- *Base de datos remota (PostgreSQL con Docker):* Se ocupará de gestionar los usuarios. Su conexión se establecerá mediante un archivo *dockerCompose.yml*.
- *Base de datos remota (MongoDB):* Se ocupará de gestionar los movimientos de los clientes. Su conexión se establecerá mediante un archivo *application.properties* y se utilizará un archivo *dockerCompose.yml* para su despliegue.


## Importación y Exportación de Datos

El sistema permite la importación y exportación de datos para facilitar la gestión de la información de clientes y movimientos en los siguientes formatos:

- *PDF*
- *JSON*
- *CSV*

A parte, podremos realizar una copia de seguridad en formato *ZIP*.


## Lenguajes y Tecnologías

- *Java*
- *Docker*
- *PostgreSQL*
- *MongoDB*
- *Postman*
- *Retrofit*
- *Git*
- *GitFlow*
- *Gradle*
- *SLF4J con Logback*
- *Swagger*
- *JUnit*
- *Mockito*


## Calidad y Pruebas

El proyecto implementa diversas prácticas y herramientas para asegurar la calidad y el correcto funcionamiento del código. A continuación se describen los principales enfoques utilizados:

- *Pruebas Unitarias*
- *Pruebas de Integración*
- *Mockito*
- *JUnit*
- *Cobertura de Código*


## Enlace al video




## Autores del programa

<table align="center">
  <tr>
    <td align="center">
      <a href="https://github.com/ngalvez0910">
        <img src="https://avatars.githubusercontent.com/u/145333876" width="70" height="70" style="border-radius: 50%;" alt="Natalia González Álvarez"/>
        <br/>
        <sub><b>Natalia</b></sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/germangfc">
        <img src="https://avatars.githubusercontent.com/u/147338370" width="70" height="70" style="border-radius: 50%;" alt="German"/>
        <br/>
        <sub><b>German</b></sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/jaimeleon10">
        <img src="https://avatars.githubusercontent.com/u/113149992" width="70" height="70" style="border-radius: 50%;" alt="Jaime León"/>
        <br/>
        <sub><b>Jaime</b></sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/Alba448">
        <img src="https://avatars.githubusercontent.com/u/146001599" width="70" height="70" style="border-radius: 50%;" alt="Alba García"/>
        <br/>
        <sub><b>Alba</b></sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/wolverine307mda">
        <img src="https://avatars.githubusercontent.com/u/146002100" width="70" height="70" style="border-radius: 50%;" alt="Mario de Domingo Alvarez"/>
        <br/>
        <sub><b>Mario</b></sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/alvarito304">
        <img src="https://avatars.githubusercontent.com/u/114983881?v=4" width="70" height="70" style="border-radius: 50%;" alt="Álvaro Herrero Tamayo"/>
        <br/>
        <sub><b>Álvaro</b></sub>
      </a>
    </td>
  </tr>
</table>