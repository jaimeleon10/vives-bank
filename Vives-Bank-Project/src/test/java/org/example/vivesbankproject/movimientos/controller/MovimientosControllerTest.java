package org.example.vivesbankproject.movimientos.controller;


import static org.mockito.ArgumentMatchers.any;
/*

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ExtendWith(MockitoExtension.class)
class MovimientosControllerTest {

    private static final String ENDPOINT = "${api.version}/movimientos";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private MovimientosService movimientosService;

    @Autowired
    private JacksonTester<Movimientos> jsonMovimiento;

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public MovimientosControllerTest(MovimientosService movimientosService) {
        this.movimientosService = movimientosService;
        mapper.registerModule(new JavaTimeModule());
    }

    private Cliente cliente;

    private Movimientos movimiento;

    private ObjectId movimientoId;

    private String clienteId;

    @BeforeEach
    void setUp() {

        movimientoId = new ObjectId();

        clienteId = IdGenerator.generarId();

        cliente = Cliente.builder()
                .id(clienteId)
                .dni("12345678A")
                .nombre("John")
                .apellidos("Doe")
                .email("john.doe@example.com")
                .telefono("123456789")
                .fotoPerfil("perfil.jpg")
                .fotoDni("dni.jpg")
                .cuentas(Set.of())
                .user(new User())
                .idMovimientos(movimientoId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        movimiento = Movimientos.builder()
                .id(movimientoId)
                .idUsuario(UUID.randomUUID())
                .cliente(cliente)
                .transacciones(new ArrayList<>())
                .isDeleted(false)
                .totalItems(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

    }

    @Test
    void getMovimientos() throws Exception {
        var movimientosList = List.of(movimiento);
        var pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
        var page = new PageImpl<>(movimientosList);

        when(movimientosService.getAll(any(Pageable.class))).thenReturn(page);

        MockHttpServletResponse response = mockMvc.perform(
                        get(ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        PageResponse<Movimientos> res = mapper.readValue(response.getContentAsString(), new TypeReference<>() {
        });

        // Assert
        assertAll(
                () -> assertEquals(200, response.getStatus()),
                () -> assertEquals(1, res.content().size())
        );

        verify(movimientosService, times(1)).getAll(any(Pageable.class));

    }

    @Test
    void getMovimientoById() {
    }

    @Test
    void getMovimientoByClienteId() {
    }

    @Test
    void createOrUpdateMovimientos() {
    }
}

 */