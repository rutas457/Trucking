package com.training.Trucking.repository;

import com.training.Trucking.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RouteRepository extends JpaRepository<Route, Long> {
    Optional<Route> findByStartCityAndEndCity(String startCity, String endCity);
}
