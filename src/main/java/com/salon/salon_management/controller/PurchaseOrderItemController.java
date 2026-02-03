package com.salon.salon_management.controller;

import com.salon.salon_management.entity.PurchaseOrderItem;
import com.salon.salon_management.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-order-items")
@CrossOrigin(origins = "*")
public class PurchaseOrderItemController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @PostMapping
    public PurchaseOrderItem addPurchaseOrderItem(@RequestBody PurchaseOrderItem item) {
        // Assuming item comes with a purchaseOrder object or ID in it.
        // Ideally we'd have a DTO or pass orderId in the item or URL.
        // But based on the signature in the image: quantity, unitPrice,
        // purchaseOrderId, productId
        if (item.getPurchaseOrder() != null && item.getPurchaseOrder().getPurchaseOrderId() != null) {
            return purchaseOrderService.addItemToOrder(item.getPurchaseOrder().getPurchaseOrderId(), item);
        }
        return null;
    }

    @GetMapping("/order/{id}")
    public List<PurchaseOrderItem> getItemsByOrder(@PathVariable Long id) {
        return purchaseOrderService.getItemsByOrder(id);
    }
}
