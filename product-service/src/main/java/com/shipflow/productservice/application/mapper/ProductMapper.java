package com.shipflow.productservice.application.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

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
}
