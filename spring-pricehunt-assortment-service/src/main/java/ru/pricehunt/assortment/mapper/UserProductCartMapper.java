package ru.pricehunt.assortment.mapper;

import org.mapstruct.Mapper;
import ru.pricehunt.assortment.dto.UserProductCartDTO;
import ru.pricehunt.assortment.model.UserProductCart;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface UserProductCartMapper {

    UserProductCartDTO userProductCartToUserProductCartDTO(UserProductCart userProductCart);

    UserProductCart userProductCartDTOToUserProductCart(UserProductCartDTO userProductCartDTO);
}
