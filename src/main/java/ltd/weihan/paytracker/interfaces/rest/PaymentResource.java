package ltd.weihan.paytracker.interfaces.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import ltd.weihan.paytracker.application.PaymentService;
import ltd.weihan.paytracker.domain.model.Payment;
import ltd.weihan.paytracker.interfaces.rest.request.CreatePaymentRequest;
import ltd.weihan.paytracker.interfaces.rest.request.UpdatePaymentRequest;
import ltd.weihan.paytracker.interfaces.rest.response.ApiResponse;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 支付记录REST接口
 * 提供支付记录的CRUD操作和文件上传功能
 */
@Path("/api/v1/payments")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Payment", description = "支付记录管理接口")
public class PaymentResource {
    
    @Inject
    PaymentService paymentService;
    
    @GET
    @Operation(summary = "获取所有支付记录")
    @APIResponse(
            responseCode = "200",
            description = "成功获取支付记录列表",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    public Response getAllPayments(
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        List<Payment> payments = paymentService.findAllPayments();
        return Response.ok(new ApiResponse<>(true, "Successfully retrieved payments", payments)).build();
    }
    
    @GET
    @Path("/{id}")
    @Operation(summary = "根据ID获取支付记录")
    @APIResponse(
            responseCode = "200",
            description = "成功获取支付记录",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    @APIResponse(
            responseCode = "404",
            description = "支付记录不存在"
    )
    public Response getPayment(
            @Parameter(description = "支付记录ID", required = true)
            @PathParam("id") Long id) {
        return paymentService.findPaymentById(id)
                .map(payment -> Response.ok(new ApiResponse<>(true, "Successfully retrieved payment", payment)).build())
                .orElse(Response.status(Response.Status.NOT_FOUND)
                        .entity(new ApiResponse<>(false, "Payment not found", null))
                        .build());
    }
    
    @POST
    @Operation(summary = "创建新的支付记录")
    @APIResponse(
            responseCode = "201",
            description = "支付记录创建成功",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    public Response createPayment(@Valid @NotNull CreatePaymentRequest request) {
        try {
            Payment payment = paymentService.createPayment(
                    request.getAmount(),
                    request.getPaymentMethod(),
                    request.getCategory(),
                    request.getDescription(),
                    request.getPaymentDate(),
                    request.getUserId()
            );
            return Response.created(
                    UriBuilder.fromResource(PaymentResource.class)
                            .path(String.valueOf(payment.id))
                            .build())
                    .entity(new ApiResponse<>(true, "Payment created successfully", payment))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiResponse<>(false, e.getMessage(), null))
                    .build();
        }
    }
    
    @POST
    @Path("/{id}/proofs")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Operation(summary = "上传支付凭证")
    @APIResponse(
            responseCode = "201",
            description = "支付凭证上传成功",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    public Response uploadPaymentProof(
            @PathParam("id") Long paymentId,
            @RestForm("file") FileUpload file) {
        try {
            // 创建上传目录
            String uploadDir = "uploads/" + LocalDateTime.now().getYear() + "/" + 
                             LocalDateTime.now().getMonthValue();
            Files.createDirectories(Paths.get(uploadDir));
            
            // 生成唯一文件名
            String originalFilename = file.fileName();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = UUID.randomUUID().toString() + fileExtension;
            java.nio.file.Path filePath = Paths.get(uploadDir, newFilename);

            // 保存文件
            Files.copy(file.filePath(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // 添加支付凭证
            Payment payment = paymentService.addPaymentProof(
                    paymentId,
                    filePath.toString(),
                    Files.probeContentType(filePath),
                    originalFilename
            );
            
            return Response.status(Response.Status.CREATED)
                    .entity(new ApiResponse<>(true, "Proof uploaded successfully", payment))
                    .build();
        } catch (IOException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiResponse<>(false, "Failed to upload file: " + e.getMessage(), null))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiResponse<>(false, e.getMessage(), null))
                    .build();
        }
    }
    
    @PUT
    @Path("/{id}")
    @Operation(summary = "更新支付记录")
    @APIResponse(
            responseCode = "200",
            description = "支付记录更新成功",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    public Response updatePayment(
            @PathParam("id") Long id,
            @Valid @NotNull UpdatePaymentRequest request) {
        try {
            Payment payment = paymentService.updatePayment(
                    id,
                    request.getAmount(),
                    request.getPaymentMethod(),
                    request.getCategory(),
                    request.getDescription()
            );
            return Response.ok(new ApiResponse<>(true, "Payment updated successfully", payment)).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiResponse<>(false, e.getMessage(), null))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiResponse<>(false, e.getMessage(), null))
                    .build();
        }
    }
    
    @DELETE
    @Path("/{id}")
    @Operation(summary = "删除支付记录")
    @APIResponse(
            responseCode = "204",
            description = "支付记录删除成功"
    )
    public Response deletePayment(@PathParam("id") Long id) {
        try {
            paymentService.deletePayment(id);
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ApiResponse<>(false, e.getMessage(), null))
                    .build();
        }
    }
    
    @GET
    @Path("/users/{userId}")
    @Operation(summary = "获取用户的所有支付记录")
    @APIResponse(
            responseCode = "200",
            description = "成功获取用户的支付记录",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    public Response getPaymentsByUser(
            @PathParam("userId") Long userId,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        List<Payment> payments = paymentService.findPaymentsByUserId(userId);
        return Response.ok(new ApiResponse<>(true, "Successfully retrieved user payments", payments)).build();
    }
    
    @GET
    @Path("/categories/{category}")
    @Operation(summary = "获取指定类别的支付记录")
    @APIResponse(
            responseCode = "200",
            description = "成功获取指定类别的支付记录",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    public Response getPaymentsByCategory(
            @PathParam("category") String category,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        List<Payment> payments = paymentService.findPaymentsByCategory(category);
        return Response.ok(new ApiResponse<>(true, "Successfully retrieved payments by category", payments)).build();
    }
    
    @GET
    @Path("/search")
    @Operation(summary = "按日期范围搜索支付记录")
    @APIResponse(
            responseCode = "200",
            description = "成功获取指定日期范围的支付记录",
            content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    public Response searchPayments(
            @QueryParam("startDate") String startDate,
            @QueryParam("endDate") String endDate,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {
        try {
            List<Payment> payments = paymentService.findPaymentsByDateRange(
                    LocalDateTime.parse(startDate),
                    LocalDateTime.parse(endDate)
            );
            return Response.ok(new ApiResponse<>(true, "Successfully retrieved payments by date range", payments)).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ApiResponse<>(false, "Invalid date format: " + e.getMessage(), null))
                    .build();
        }
    }
} 