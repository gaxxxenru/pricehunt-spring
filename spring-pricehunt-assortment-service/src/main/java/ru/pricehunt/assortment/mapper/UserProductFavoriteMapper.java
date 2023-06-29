package ru.pricehunt.assortment.mapper;

import org.mapstruct.Mapper;
import ru.pricehunt.assortment.dto.UserProductFavoriteDTO;
import ru.pricehunt.assortment.model.UserProductFavorite;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface UserProductFavoriteMapper {

    UserProductFavoriteDTO userProductFavoriteToUserProductFavoriteDTO(UserProductFavorite userProductFavorite);

    UserProductFavorite userProductFavoriteDTOToUserProductFavorite(UserProductFavoriteDTO userProductFavoriteDTO);
}
