package de.test.utils;

import org.junit.After;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;


public abstract class BaseTest {

   @Autowired
   @Qualifier("appJdbcTemplate")
   protected JdbcTemplate jdbcTemplate;

   protected TableContent getTableContent() {

      TableContent tableContent = DatabaseTestUtility.extractTableContent("Car", jdbcTemplate);
      System.out.println(DatabaseTestUtility.tableContentToString(tableContent));
      return tableContent;
   }

   @After
   public void analyse() {

      TableContent tableContent = getTableContent();

      Assert.assertEquals("We expect 4 processed items in the table", 4, tableContent.getRows().size());

      for (String row : tableContent.getRows()) {
         Assert.assertTrue("Should contain \"processed\" in name!", row.contains("processed"));
      }
   }
}