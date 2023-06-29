package ru.pricehunt.assortment.retailer;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.pricehunt.assortment.dto.RetailerDTO;
import ru.pricehunt.assortment.mapper.RetailerMapper;
import ru.pricehunt.assortment.model.Retailer;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/retailer")
@Tag(name = "Retailer Controller", description = "Контроллер для работы с ритейлерами")
public class RetailerController {
    private final RetailerService retailerService;
    private final RetailerMapper retailerMapper;

    @GetMapping
    @Operation(summary = "Список всех ритейлеров", description = "Возвращает список всех ритейлеров")
    public List<RetailerDTO> getAllRetailers() {
        return retailerMapper.retailersToRetailerDTOs(retailerService.findAll());
    }


    @GetMapping("/{slug}")
    @Operation(summary = "Ритейлер по slug", description = "Возвращает ритейлера по slug")
    public RetailerDTO getRetailerBySlug(@PathVariable String slug) {
        return retailerMapper.retailerToRetailerDTO(retailerService.findBySlug(slug));
    }

    @PutMapping("/{slug}")
    @Operation(summary = "Обновление ритейлера", description = "Обновляет ритейлера по slug")
    public RetailerDTO updateRetailer(@PathVariable String slug, @RequestBody RetailerDTO retailerDTO) {
        Retailer retailer = retailerMapper.retailerDTOToRetailer(retailerDTO);
        return retailerMapper.retailerToRetailerDTO(retailerService.update(slug, retailer));
    }

    @PostMapping
    @Operation(summary = "Создание ритейлера", description = "Создает ритейлера")
    public RetailerDTO createRetailer(@RequestBody RetailerDTO retailerDTO) {
        Retailer retailer = retailerMapper.retailerDTOToRetailer(retailerDTO);
        return retailerMapper.retailerToRetailerDTO(retailerService.save(retailer));
    }

    @DeleteMapping("/{slug}")
    @Operation(summary = "Удаление ритейлера", description = "Удаляет ритейлера по slug")
    public RetailerDTO deleteRetailer(@PathVariable String slug) {
        return retailerMapper.retailerToRetailerDTO(retailerService.delete(slug));
    }
}
