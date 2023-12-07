package com.MicroserviceP1.OrderService.Service;

import com.MicroserviceP1.OrderService.Dto.InventoryResponse;
import com.MicroserviceP1.OrderService.Dto.OrderLineItemsDto;
import com.MicroserviceP1.OrderService.Dto.OrderRequest;
import com.MicroserviceP1.OrderService.Model.Order;
import com.MicroserviceP1.OrderService.Model.OrderLineItems;
import com.MicroserviceP1.OrderService.Repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final Tracer tracer;

    public String placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodes = order.getOrderLineItemsList()
                .stream()
                .map(OrderLineItems::getSkuCode).toList();

        Span inventoryServiceLookup = tracer.nextSpan().name("InventoryServiceLookup");
       try(Tracer.SpanInScope spanInScope= tracer.withSpan(inventoryServiceLookup.start())){
           //Call the inventory service and check if product in stock or not

           InventoryResponse[] inventoryResponseArray  = webClientBuilder.build().get()
                   .uri("http://inventory-service/api/inventory",
                           uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                   .retrieve()
                   .bodyToMono(InventoryResponse[].class)
                   .block();

           boolean allProductsInStock = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::isIsInstock);

           if(allProductsInStock){
               orderRepository.save(order);
               return "Order saved successfully";

           }else {
               throw new IllegalArgumentException("The product is not available in stock, Try later");
           }
       }finally {
            inventoryServiceLookup.end();
       }


    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}
