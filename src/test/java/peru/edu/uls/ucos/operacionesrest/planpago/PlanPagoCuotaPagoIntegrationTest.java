package peru.edu.uls.ucos.operacionesrest.planpago;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import peru.edu.uls.ucos.operacionesrest.cliente.Cliente;
import peru.edu.uls.ucos.operacionesrest.cliente.ClienteRepository;
import peru.edu.uls.ucos.operacionesrest.cuota.Cuota;
import peru.edu.uls.ucos.operacionesrest.cuota.CuotaRepository;
import peru.edu.uls.ucos.operacionesrest.cuota.CuotaRequest;
import peru.edu.uls.ucos.operacionesrest.pago.Pago;
import peru.edu.uls.ucos.operacionesrest.pago.PagoRepository;
import peru.edu.uls.ucos.operacionesrest.pago.PagoRequest;
import peru.edu.uls.ucos.operacionesrest.venta.EstadoVenta;
import peru.edu.uls.ucos.operacionesrest.venta.Venta;
import peru.edu.uls.ucos.operacionesrest.venta.VentaRepository;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PlanPagoCuotaPagoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private PlanPagoRepository planPagoRepository;

    @Autowired
    private CuotaRepository cuotaRepository;

    @Autowired
    private PagoRepository pagoRepository;

    private PlanPago planPago;
    private Cuota cuota;
    private Pago pago;

    @BeforeEach
    void prepararDatos() {
        Venta venta = crearVenta();

        planPago = new PlanPago();
        planPago.setTipoPago("CUOTAS");
        planPago.setCantCuotas(2);
        planPago.setVenta(venta);
        venta.agregarPlanPago(planPago);

        cuota = new Cuota();
        cuota.setNumeroCuota(1);
        cuota.setMonto(50.0);
        cuota.setFechaVencimiento(LocalDateTime.of(2026, 11, 15, 0, 0));
        cuota.setEstado("PAGADA");
        planPago.agregarCuota(cuota);
        planPagoRepository.saveAndFlush(planPago);

        pago = new Pago();
        pago.setMetodo("TARJETA");
        pago.setMonto(50.0);
        pago.setFechaPago(LocalDateTime.of(2026, 10, 15, 0, 0));
        pago.setCuota(cuota);
        cuota.setPago(pago);
        pagoRepository.saveAndFlush(pago);
    }

    @Test
    @DisplayName("A: consulta un plan de pago por ID")
    void consultarPlanPagoPorId() throws Exception {
        mockMvc.perform(get("/api/planes-pago/{id}", planPago.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(planPago.getId()))
                .andExpect(jsonPath("$.tipoPago").value("CUOTAS"))
                .andExpect(jsonPath("$.cuotas[0].id").value(cuota.getId()));
    }

    @Test
    @DisplayName("A: consulta una cuota por ID")
    void consultarCuotaPorId() throws Exception {
        mockMvc.perform(get("/api/cuotas/{id}", cuota.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cuota.getId()))
                .andExpect(jsonPath("$.numeroCuota").value(1))
                .andExpect(jsonPath("$.planPagoId").value(planPago.getId()));
    }

    @Test
    @DisplayName("A: consulta un pago por ID")
    void consultarPagoPorId() throws Exception {
        mockMvc.perform(get("/api/pagos/{id}", pago.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(pago.getId()))
                .andExpect(jsonPath("$.metodo").value("TARJETA"))
                .andExpect(jsonPath("$.cuotaId").value(cuota.getId()));
    }

    @Test
    @DisplayName("B: registra un plan de pago con sus cuotas")
    void registrarPlanPagoConCuotas() throws Exception {
        Venta venta = crearVenta();
        PlanPagoRequest request = new PlanPagoRequest(
                "CUOTAS",
                2,
                venta.getId(),
                List.of(
                        new PlanPagoRequest.CuotaPlanPagoRequest(
                                1, 75.0, LocalDateTime.of(2026, 11, 15, 0, 0), "PENDIENTE"),
                        new PlanPagoRequest.CuotaPlanPagoRequest(
                                2, 75.0, LocalDateTime.of(2026, 12, 15, 0, 0), "PENDIENTE")
                )
        );

        String response = mockMvc.perform(post("/api/planes-pago")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.ventaId").value(venta.getId()))
                .andExpect(jsonPath("$.cuotas.length()").value(2))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long planPagoId = objectMapper.readTree(response).get("id").asLong();
        PlanPago persistedPlanPago = planPagoRepository.findById(planPagoId).orElseThrow();
        assertThat(persistedPlanPago.getCuotas()).hasSize(2);
        assertThat(persistedPlanPago.getCuotas())
                .allSatisfy(persistedCuota -> assertThat(persistedCuota.getPlanPago().getId()).isEqualTo(planPagoId));
    }

    @Test
    @DisplayName("B: registra una cuota en un plan de pago")
    void registrarCuotaEnPlanPago() throws Exception {
        CuotaRequest request = new CuotaRequest(
                2,
                50.0,
                LocalDateTime.of(2026, 12, 15, 0, 0),
                "PENDIENTE",
                planPago.getId()
        );

        mockMvc.perform(post("/api/cuotas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numeroCuota").value(2))
                .andExpect(jsonPath("$.planPagoId").value(planPago.getId()));
    }

    @Test
    @DisplayName("B: registra un pago para una cuota pendiente")
    void registrarPagoParaCuotaPendiente() throws Exception {
        Cuota cuotaPendiente = new Cuota();
        cuotaPendiente.setNumeroCuota(2);
        cuotaPendiente.setMonto(50.0);
        cuotaPendiente.setFechaVencimiento(LocalDateTime.of(2026, 12, 15, 0, 0));
        cuotaPendiente.setEstado("PENDIENTE");
        cuotaPendiente.setPlanPago(planPago);
        cuotaPendiente = cuotaRepository.saveAndFlush(cuotaPendiente);

        PagoRequest request = new PagoRequest("EFECTIVO", 50.0, cuotaPendiente.getId());

        mockMvc.perform(post("/api/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.metodo").value("EFECTIVO"))
                .andExpect(jsonPath("$.monto").value(50.0))
                .andExpect(jsonPath("$.cuotaId").value(cuotaPendiente.getId()));
    }

    @Test
    @DisplayName("C: devuelve not found al consultar recursos inexistentes")
    void consultarRecursosInexistentesDevuelveNotFound() throws Exception {
        mockMvc.perform(get("/api/planes-pago/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Plan de pago no encontrado con el ID: " + Long.MAX_VALUE));

        mockMvc.perform(get("/api/cuotas/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Cuota no encontrada con el ID: " + Long.MAX_VALUE));

        mockMvc.perform(get("/api/pagos/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Pago no encontrado con el ID: " + Long.MAX_VALUE));
    }

    @Test
    @DisplayName("C: rechaza planes duplicados y datos inválidos")
    void registrarPlanPagoManejaDuplicadosYDatosInvalidos() throws Exception {
        PlanPagoRequest planDuplicado = new PlanPagoRequest(
                "CUOTAS", 2, planPago.getVenta().getId());
        mockMvc.perform(post("/api/planes-pago")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(planDuplicado)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("La venta ya tiene un plan de pago."));

        PlanPagoRequest planInvalido = new PlanPagoRequest("", 2, planPago.getVenta().getId());
        mockMvc.perform(post("/api/planes-pago")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(planInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El tipo de pago es obligatorio."));
    }

    @Test
    @DisplayName("C: rechaza pagos duplicados y montos inválidos")
    void registrarPagoManejaDuplicadosYDatosInvalidos() throws Exception {
        PagoRequest pagoDuplicado = new PagoRequest("TARJETA", 50.0, cuota.getId());
        mockMvc.perform(post("/api/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pagoDuplicado)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("La cuota ya tiene un pago registrado."));

        PagoRequest pagoInvalido = new PagoRequest("TARJETA", 0.0, cuota.getId());
        mockMvc.perform(post("/api/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pagoInvalido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("El monto del pago debe ser un número finito mayor que cero."));
    }

    @Test
    @DisplayName("C: rechaza cuotas con datos inválidos")
    void registrarCuotaManejaDatosInvalidos() throws Exception {
        CuotaRequest cuotaInvalida = new CuotaRequest(
                0,
                50.0,
                LocalDateTime.of(2026, 12, 15, 0, 0),
                "PENDIENTE",
                planPago.getId()
        );

        mockMvc.perform(post("/api/cuotas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cuotaInvalida)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("El número de cuota debe ser mayor que cero."));
    }

    private Venta crearVenta() {
        Cliente cliente = clienteRepository.saveAndFlush(new Cliente(
                "Cliente de prueba",
                UUID.randomUUID().toString(),
                "test@example.com"
        ));
        Venta venta = new Venta(150.0, EstadoVenta.PENDIENTE, cliente);
        venta.setFecha(LocalDateTime.now());
        return ventaRepository.saveAndFlush(venta);
    }
}
