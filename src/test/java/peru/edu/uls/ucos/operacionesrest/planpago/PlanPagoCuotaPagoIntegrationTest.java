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
import peru.edu.uls.ucos.operacionesrest.pago.Pago;
import peru.edu.uls.ucos.operacionesrest.pago.PagoRepository;
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
    private PagoRepository pagoRepository;

    private PlanPago planPago;
    private Cuota cuota;
    private Pago pago;

    @BeforeEach
    void prepararDatos() {
        Venta venta = crearVenta();

        planPago = new PlanPago();
        planPago.setTipoPago("CUOTAS");
        planPago.setCantCuotas(1);
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
