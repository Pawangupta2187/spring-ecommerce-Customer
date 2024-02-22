package com.customer.customer.entities.cart.DTO;

import com.customer.customer.entities.products.DTO.ViewVariationDTO;
import lombok.Data;

@Data
public class ViewCartDTO {
    private Long quantity;
    private ViewVariationDTO variation;
    }
