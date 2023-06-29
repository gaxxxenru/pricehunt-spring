package ru.pricehunt.assortment.mapper;

import org.mapstruct.Mapper;
import ru.pricehunt.assortment.dto.FeatureDTO;
import ru.pricehunt.assortment.model.Feature;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FeatureMapper {
    FeatureDTO featureToFeatureDTO(Feature feature);
    Feature featureDTOToFeature(FeatureDTO featureDto);

    List<FeatureDTO> featuresToFeatureDTOs(List<Feature> features);
    List<Feature> featureDTOsToFeatures(List<FeatureDTO> featureDtos);

}
