package com.space.controller;

import com.space.exeption.WrongRequestException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@ResponseBody
public class ShipController {
    private final ShipService shipService;

    @Autowired
    public ShipController(ShipService shipService) {
        this.shipService = shipService;
    }

    @GetMapping(value = "/rest/ships")
    public List<Ship> getAllShips(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String planet,
            @RequestParam(required = false) ShipType shipType,
            @RequestParam(required = false) Long after,
            @RequestParam(required = false) Long before,
            @RequestParam(required = false) Boolean isUsed,
            @RequestParam(required = false) Double minSpeed,
            @RequestParam(required = false) Double maxSpeed,
            @RequestParam(required = false) Integer minCrewSize,
            @RequestParam(required = false) Integer maxCrewSize,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double maxRating,
            @RequestParam(required = false) ShipOrder order,
            @RequestParam(required = false) Integer pageNumber,
            @RequestParam(required = false) Integer pageSize
    ){
        List<Ship> Ships = shipService.getShipList(name, planet, shipType, after, before, isUsed, minSpeed,
                maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating);
        return shipService.filteredShips(Ships, order, pageNumber, pageSize);
    }

    @GetMapping(value ="/rest/ships/count")
    public Integer getShipsCount(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String planet,
            @RequestParam(required = false) ShipType shipType,
            @RequestParam(required = false) Long after,
            @RequestParam(required = false) Long before,
            @RequestParam(required = false) Boolean isUsed ,
            @RequestParam(required = false) Double minSpeed,
            @RequestParam(required = false) Double maxSpeed,
            @RequestParam(required = false) Integer minCrewSize,
            @RequestParam(required = false) Integer maxCrewSize,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double maxRating
    ){
        return shipService.getShipList(name, planet, shipType,after, before, isUsed, minSpeed,maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating).size();
    }

    @PostMapping(value = "/rest/ships")
    @ResponseBody
    public ResponseEntity<Ship> createShip (@RequestBody Ship ship){
        if (ship==null) {
            throw new WrongRequestException();
        }
        Ship createdShip=null;
        try {createdShip = shipService.createShip(ship);
        }catch (NullPointerException e){
            throw new  WrongRequestException();
        }
        if (createdShip==null){
            throw  new WrongRequestException();
        }
        return new ResponseEntity<>(createdShip,HttpStatus.OK);
    }

    @GetMapping(value = "/rest/ships/{id}")
    @ResponseBody
    public Ship getShipById(@PathVariable Long id){
        if(!isValidId(id)){
            throw new  WrongRequestException();
        }
        return shipService.getShipById(id);
    }

    @PostMapping(value = "/rest/ships/{id}")
    @ResponseBody
    public Ship updateShip(@RequestBody Ship ship ,@PathVariable Long id){
        if (!isValidId(id)){
            throw new WrongRequestException();
        }
        return shipService.updateShip(ship,id);
    }

    @DeleteMapping(value = "/rest/ships/{id}")
    public void deleteShipById(@PathVariable Long id){
        if (!isValidId(id)){
            throw  new WrongRequestException();
        }
        shipService.deleteById(id);
    }

    private Boolean isValidId(Long id){
        return id!=null&&id==Math.floor(id)&&id>0;
    }
}
