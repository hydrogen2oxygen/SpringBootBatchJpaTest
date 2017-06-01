package de.test.utils;

import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.hsqldb.jdbc.JDBCBlobClient;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

public class DatabaseTestUtility {

   private DatabaseTestUtility() {

   }

   public static TableContent extractTableContent(String tableOrSelect, JdbcTemplate jdbcTemplate) {

      final TableContent tableContent = new TableContent();
      String sql = null;

      if (tableOrSelect.toLowerCase().contains("select ")) {
         sql = tableOrSelect;
      } else {
         sql = "select * from " + tableOrSelect;
      }

      ResultSetExtractor<String> rse = new ResultSetExtractor<String>() {

         public String result;

         @Override
         public String extractData(ResultSet rs) throws SQLException, DataAccessException {

            StringBuffer row = new StringBuffer();

            for (int x = 1; x < rs.getMetaData().getColumnCount() + 1; x++) {

               String label = rs.getMetaData().getColumnLabel(x);
               row.append(label);
               row.append(";");

               tableContent.getHeaderList().add(label);

               tableContent.getColumnSizes().put(x - 1, label.length());
            }

            tableContent.setHeader(row.toString());

            while (rs.next()) {

               row = new StringBuffer();

               List<String> rows = new ArrayList<>();

               for (int x = 1; x < rs.getMetaData().getColumnCount() + 1; x++) {

                  String value = "";

                  try {
                     if (rs.getMetaData().getColumnType(x) == 2004) {
                        JDBCBlobClient blob = (JDBCBlobClient) rs.getObject(x);
                        StringWriter writer = new StringWriter();
                        IOUtils.copy(blob.getBinaryStream(), writer);
                        value = writer.toString();
                     } else {
                        value = rs.getString(x);
                     }
                  } catch(Exception e) {
                     System.err.println(rs.getMetaData().getColumnType(x) + " " + rs.getObject(x));
                  }

                  row.append(value);
                  row.append(";");

                  rows.add(value);

                  setColumnSize(x - 1, value);
               }

               tableContent.getRowList().add(rows);

               tableContent.getRows().add(row.toString());
            }

            result = row.toString();
            return result;
         }

         private void setColumnSize(int rowNumber, String value) {

            Integer length = tableContent.getColumnSizes().get(rowNumber);

            if (value != null && value.length() > length) {
               tableContent.getColumnSizes().put(rowNumber, value.length());
            }
         }
      };

      jdbcTemplate.query(sql, rse);

      return tableContent;
   }

   public static String tableContentToString(TableContent tableContent) {

      StringBuilder str = new StringBuilder();

      for (int x = 0; x < tableContent.getHeaderList().size(); x++) {

         str.append(fillUpEmptySpace(tableContent.getHeaderList().get(x), tableContent.getColumnSizes().get(x)));
         str.append("|");
      }

      str.append("\n");

      for (List<String> row : tableContent.getRowList()) {

         int x = 0;

         for (String value : row) {
            str.append(fillUpEmptySpace(value, tableContent.getColumnSizes().get(x)));
            str.append("|");
            x++;
         }
         str.append("\n");
      }

      return str.toString();
   }

   private static String fillUpEmptySpace(String str, Integer size) {

      if (str == null) {
         str = "";
      }

      while (str.length() < size) {
         str += " ";
      }

      return str;
   }
}
