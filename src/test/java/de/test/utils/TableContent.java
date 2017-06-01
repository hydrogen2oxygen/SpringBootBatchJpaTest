package de.test.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TableContent {

   private Map<Integer, Integer> columnSizes = new HashMap<>();

   private String header;

   private List<String> rows = new ArrayList<String>();

   private List<String> headerList = new ArrayList<>();

   private List<List<String>> rowList = new ArrayList<>();
}
