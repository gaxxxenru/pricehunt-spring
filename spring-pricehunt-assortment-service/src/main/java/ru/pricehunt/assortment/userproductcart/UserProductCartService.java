package ru.pricehunt.assortment.userproductcart;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pricehunt.assortment.model.Product;
import ru.pricehunt.assortment.model.UserProductCart;
import ru.pricehunt.assortment.product.ProductService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserProductCartService {

    private final UserProductCartRepository userProductCartRepository;
    private final ProductService productService;

    public UserProductCart findCartByUserEmail(String email) {
        UserProductCart userProductCart = userProductCartRepository.findCartWithImages(email);
        if (userProductCart == null) {
            return UserProductCart.builder().userEmail(email).products(List.of()).build();
        }
        return userProductCart;
    }

    @Transactional
    public UserProductCart addProductToCart(String email, String productSlug) {
        Product product = productService.findBySlug(productSlug);
        if (product == null) {
            throw new IllegalArgumentException("Товар не найден");
        }
        UserProductCart userProductCart = userProductCartRepository.findCartWithImages(email);
        if (userProductCart == null) {
            userProductCart = UserProductCart.builder().userEmail(email).products(List.of(product)).build();
        } else {
            if (userProductCart.getProducts().contains(product)) {
                throw new IllegalArgumentException("Товар уже добавлен в корзину");
            }
            userProductCart.getProducts().add(product);
        }
        return userProductCartRepository.save(userProductCart);
    }

    @Transactional
    public UserProductCart removeProductFromCart(String email, String productSlug) {
        Product product = productService.findBySlug(productSlug);
        if (product == null) {
            throw new IllegalArgumentException("Товар не найден");
        }
        UserProductCart userProductCart = userProductCartRepository.findCartWithImages(email);
        if (userProductCart == null || !userProductCart.getProducts().contains(product)) {
            throw new IllegalArgumentException("Товар не найден");
        }
        userProductCart.getProducts().remove(product);
        return userProductCartRepository.save(userProductCart);
    }
}
