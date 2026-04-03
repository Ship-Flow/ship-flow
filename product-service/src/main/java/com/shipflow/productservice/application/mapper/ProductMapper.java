package com.shipflow.productservice.application.mapper;

import org.mapstruct.Mapper;

import com.shipflow.productservice.application.dto.response.StockInfoResponse;
import com.shipflow.productservice.domain.model.Product;
import com.shipflow.productservice.presentation.dto.response.ProductCreateResponse;
import com.shipflow.productservice.presentation.dto.response.ProductInfoResponse;
import com.shipflow.productservice.presentation.dto.response.ProductListResponse;
import com.shipflow.productservice.presentation.dto.response.ProductUpdateResponse;

@Mapper(componentModel = "spring")
public interface ProductMapper {
	//Entity->DTO
	ProductCreateResponse toCreateResponse(Product product);

	ProductUpdateResponse toUpdateResponse(Product product);

	ProductInfoResponse toProductInfoResponse(Product product);

	ProductListResponse toProductListResponse(Product product);
	`@Mapping`(source = "id", target = "productId")
	`@Mapping`(source = "stockInfo.stock", target = "stock")
	StockInfoResponse toStockInfoResponse(Product product);
}
