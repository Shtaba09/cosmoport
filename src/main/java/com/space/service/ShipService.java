package com.space.service;

import com.space.controller.ShipOrder;
import com.space.exeption.NotFoundException;
import com.space.exeption.WrongRequestException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShipService  {
    private final ShipRepository shipRepository;
    private final int currentYear = 3019;

    @Autowired
    public ShipService(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }



    public List<Ship> getShipList(String name, String planet, ShipType shipType, Long after, Long before, Boolean isUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize, Double minRating, Double maxRating) {
        List<Ship> filteredShips = shipRepository.findAll();
        if (name != null) {
            filteredShips = filteredShips.stream()
                    .filter(ship -> ship.getName().contains(name))
                    .collect(Collectors.toList());
        }
        if (planet != null) {
            filteredShips = filteredShips.stream()
                    .filter(ship -> ship.getPlanet().contains(planet))
                    .collect(Collectors.toList());
        }
        if (shipType != null) {
            filteredShips = filteredShips.stream()
                    .filter(ship -> ship.getShipType().equals(shipType))
                    .collect(Collectors.toList());
        }
        if (after != null) {
            filteredShips = filteredShips.stream()
                    .filter(ship -> ship.getProdDate().getTime()>=after)
                    .collect(Collectors.toList());
        }
        if (before != null) {
            filteredShips = filteredShips.stream()
                    .filter(ship -> ship.getProdDate().getTime()<=before)
                    .collect(Collectors.toList());
        }
        if (isUsed != null) {
            filteredShips = filteredShips.stream()
                    .filter(ship -> ship.getUsed().equals(isUsed))
                    .collect(Collectors.toList());
        }
        if (minSpeed != null) {
            filteredShips = filteredShips.stream()
                    .filter(ship -> ship.getSpeed() >= minSpeed)
                    .collect(Collectors.toList());
        }
        if (maxSpeed != null) {
            filteredShips = filteredShips.stream()
                    .filter(ship -> ship.getSpeed() <= maxSpeed)
                    .collect(Collectors.toList());
        }
        if (minCrewSize != null) {
            filteredShips = filteredShips.stream()
                    .filter(ship -> ship.getCrewSize() >= minCrewSize)
                    .collect(Collectors.toList());
        }
        if (maxCrewSize != null) {
            filteredShips = filteredShips.stream()
                    .filter(ship -> ship.getCrewSize() <= maxCrewSize)
                    .collect(Collectors.toList());
        }
        if (minRating != null) {
            filteredShips = filteredShips.stream()
                    .filter(ship -> ship.getRating() >= minRating)
                    .collect(Collectors.toList());
        }
        if (maxRating != null) {
            filteredShips = filteredShips.stream()
                    .filter(ship -> ship.getRating() <= maxRating)
                    .collect(Collectors.toList());
        }

        return filteredShips;
    }

    public List<Ship> filteredShips( final List<Ship> list, ShipOrder order, Integer pageNumber, Integer pageSize) {
        pageNumber = pageNumber == null ? 0 : pageNumber;
        pageSize = pageSize == null ? 3 : pageSize;
       return (List<Ship>) list.stream()
                .sorted(findComparatorByOrder(order))
                .skip(pageNumber*pageSize).limit(pageSize)
                .collect(Collectors.toList());
    }


    public Ship updateShip(Ship ship, Long id) {
        Ship shipUpdate = getShipById(id);

        if (ship == null || shipUpdate == null) {
            throw new WrongRequestException();
        }
        if (ship.getName() != null) {
            if (ship.getName().length() > 50 ||
                    ship.getName().isEmpty()) {
                throw new WrongRequestException();
            }
            shipUpdate.setName(ship.getName());
        }
        if (ship.getPlanet() != null) {
            if (ship.getPlanet().length() > 50 ||
                    ship.getPlanet().isEmpty()) {
                throw new WrongRequestException();
            }
            shipUpdate.setPlanet(ship.getPlanet());
        }
        if (ship.getShipType() != null) {
            shipUpdate.setShipType(ship.getShipType());
        }
        if (ship.getProdDate() != null) {
            if (ship.getProdDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear() < 2800 ||
                    ship.getProdDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear() > 3019) {
                throw new WrongRequestException();
            }
            shipUpdate.setProdDate(ship.getProdDate());
        }
        if (ship.getUsed() != null) {
            shipUpdate.setUsed(ship.getUsed());
        }
        if (ship.getSpeed() != null) {
            if (ship.getSpeed() < 0.01d ||
                    ship.getSpeed() > 0.99d) {
                throw new WrongRequestException();
            }
            shipUpdate.setSpeed(ship.getSpeed());
        }
        if (ship.getCrewSize() != null) {
            if (ship.getCrewSize() < 1 ||
                    ship.getCrewSize() > 9999) {
                throw new WrongRequestException();
            }
            shipUpdate.setCrewSize(ship.getCrewSize());
        }

        shipUpdate.setRating(calculatingRating(shipUpdate));
        return shipRepository.save(shipUpdate);
    }

    private double calculatingRating (Ship ship){
        int produceYear = ship.getProdDate().toInstant().atZone(ZoneId.systemDefault()).getYear();
        double speed = ship.getSpeed();
        return (double) Math.round(((80*speed*(ship.getUsed()? 0.5 : 1.0) )/(currentYear-produceYear+1))*100)/100;
    }

    public Ship createShip(Ship ship) {
        if(ship.getName()==null||
                ship.getName().isEmpty()||
                ship.getName().length()>50||
                ship.getPlanet()==null||
                ship.getPlanet().isEmpty()||
                ship.getPlanet().length()>50||
                ship.getShipType()==null||
                ship.getProdDate()==null||
                ship.getProdDate().toInstant().atZone(ZoneId.systemDefault()).getYear()>currentYear||
                ship.getProdDate().toInstant().atZone(ZoneId.systemDefault()).getYear()<2800||
                ship.getSpeed()==null||
                ship.getSpeed()<0d||
                ship.getSpeed()>0.99d||
                ship.getCrewSize()<1||
                ship.getCrewSize()>9999){
            throw new WrongRequestException();
        }
        if(ship.getUsed()==null){ship.setUsed(false);}
        ship.setRating(calculatingRating(ship));
        return shipRepository.save(ship);
    }

    public Ship getShipById(Long id) {
        if(!shipRepository.existsById(id)){
            throw  new NotFoundException();
        }
        return shipRepository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        if(!shipRepository.existsById(id)){
            throw  new NotFoundException();
        }
        shipRepository.deleteById(id);
    }

    private Comparator findComparatorByOrder(ShipOrder order){
        if (order==null){
            return Comparator.comparing(Ship::getId);
        }
        Comparator<Ship> comparator = null;
        switch (order.getFieldName()) {
            case "id":
                comparator = Comparator.comparing(Ship::getId);
                break;
            case "speed":
                comparator = Comparator.comparing(Ship::getSpeed);
                break;
            case "prodDate":
                comparator = Comparator.comparing(Ship::getProdDate);
                break;
            case "rating":
                comparator = Comparator.comparing(Ship::getRating);
        }
        return comparator;
    }
}
