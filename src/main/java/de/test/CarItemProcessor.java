package de.test;

import org.springframework.batch.item.ItemProcessor;

public class CarItemProcessor implements ItemProcessor<CarEntity, CarEntity>{

   @Override
   public CarEntity process(CarEntity item) throws Exception {

      item.setName(item.getName() + " processed");
      return item;
   }

}