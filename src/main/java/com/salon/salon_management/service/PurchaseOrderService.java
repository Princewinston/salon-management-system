package com.salon.salon_management.service;

import com.salon.salon_management.entity.PurchaseOrder;
import com.salon.salon_management.entity.PurchaseOrderItem;
import com.salon.salon_management.repository.PurchaseOrderItemRepository;
import com.salon.salon_management.repository.PurchaseOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PurchaseOrderService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private PurchaseOrderItemRepository purchaseOrderItemRepository;

    public PurchaseOrder createPurchaseOrder(PurchaseOrder order) {
        return purchaseOrderRepository.save(order);
    }

    public List<PurchaseOrder> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll();
    }

    public Optional<PurchaseOrder> getPurchaseOrderById(Long id) {
        return purchaseOrderRepository.findById(id);
    }

    public PurchaseOrder updatePurchaseOrder(Long id, PurchaseOrder orderDetails) {
        return purchaseOrderRepository.findById(id).map(order -> {
            boolean wasCompleted = "Completed".equalsIgnoreCase(order.getStatus());
            order.setOrderNumber(orderDetails.getOrderNumber());
            order.setStatus(orderDetails.getStatus());
            order.setOrderDate(orderDetails.getOrderDate());
            order.setSupplier(orderDetails.getSupplier());

            PurchaseOrder savedOrder = purchaseOrderRepository.save(order);

            // Business Logic: If status changed to Completed, increase stock
            if (!wasCompleted && "Completed".equalsIgnoreCase(savedOrder.getStatus())) {
                updateStockForOrder(savedOrder);
            }
            return savedOrder;
        }).orElse(null);
    }

    private void updateStockForOrder(PurchaseOrder order) {
        if (order.getItems() != null) {
            for (PurchaseOrderItem item : order.getItems()) {
                if (item.getProduct() != null) {
                    productService.updateStock(item.getProduct().getProductId(), item.getQuantity());
                }
            }
        }
    }

    @Autowired
    private com.salon.salon_management.service.ProductService productService;

    public PurchaseOrderItem addItemToOrder(Long orderId, PurchaseOrderItem item) {
        Optional<PurchaseOrder> order = purchaseOrderRepository.findById(orderId);
        if (order.isPresent()) {
            item.setPurchaseOrder(order.get());
            return purchaseOrderItemRepository.save(item);
        }
        return null;
    }

    public List<PurchaseOrderItem> getItemsByOrder(Long orderId) {
        Optional<PurchaseOrder> order = purchaseOrderRepository.findById(orderId);
        return order.map(PurchaseOrder::getItems).orElse(null);
    }
}
