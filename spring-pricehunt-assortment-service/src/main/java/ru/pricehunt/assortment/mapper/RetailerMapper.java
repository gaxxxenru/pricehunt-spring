package ru.pricehunt.assortment.mapper;

import org.mapstruct.Mapper;
import ru.pricehunt.assortment.dto.RetailerDTO;
import ru.pricehunt.assortment.model.Retailer;

import java.util.List;


@Mapper(componentModel = "spring", uses = {PaymentMethodMapper.class})
public interface RetailerMapper {


    RetailerDTO retailerToRetailerDTO(Retailer retailer);
    Retailer retailerDTOToRetailer(RetailerDTO retailerDTO);

    List<RetailerDTO> retailersToRetailerDTOs(List<Retailer> retailers);
    List<Retailer> retailerDTOsToRetailers(List<RetailerDTO> retailerDtos);


}
