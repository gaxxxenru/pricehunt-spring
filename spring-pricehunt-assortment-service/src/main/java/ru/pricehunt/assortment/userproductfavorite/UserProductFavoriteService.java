package ru.pricehunt.assortment.userproductfavorite;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pricehunt.assortment.model.Product;
import ru.pricehunt.assortment.model.UserProductFavorite;
import ru.pricehunt.assortment.product.ProductService;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserProductFavoriteService {

    private final UserProductFavoriteRepository userProductFavoriteRepository;
    private final ProductService productService;

    public UserProductFavorite findFavoritesByUserEmail(String email) {
        UserProductFavorite userProductFavorite = userProductFavoriteRepository.findFavortiesWithImages(email);
        if (userProductFavorite == null) {
            return UserProductFavorite.builder().userEmail(email).products(List.of()).build();
        }
        return userProductFavorite;
    }

    @Transactional
    public UserProductFavorite addProductToFavorite(String email, String productSlug) {
        Product product = productService.findBySlug(productSlug);
        if (product == null) {
            throw new IllegalArgumentException("Товар не найден");
        }
        UserProductFavorite userProductFavorite = userProductFavoriteRepository.findFavortiesWithImages(email);
        if (userProductFavorite == null) {
            userProductFavorite = UserProductFavorite.builder().userEmail(email).products(List.of(product)).build();
        } else {
            if (userProductFavorite.getProducts().contains(product)) {
                throw new IllegalArgumentException("Товар уже добавлен в избранное");
            }
            userProductFavorite.getProducts().add(product);
        }
        return userProductFavoriteRepository.save(userProductFavorite);
    }

    @Transactional
    public UserProductFavorite removeProductFromFavorite(String email, String productSlug) {
        Product product = productService.findBySlug(productSlug);
        if (product == null) {
            throw new IllegalArgumentException("Товар не найден");
        }
        UserProductFavorite userProductFavorite = userProductFavoriteRepository.findFavortiesWithImages(email);
        if (userProductFavorite == null || !userProductFavorite.getProducts().contains(product)) {
            throw new IllegalArgumentException("Товар не найден");
        }
        userProductFavorite.getProducts().remove(product);
        return userProductFavoriteRepository.save(userProductFavorite);
    }
}
