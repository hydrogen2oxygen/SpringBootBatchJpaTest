package de.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.test.utils.BaseTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class TestJobAndStep extends BaseTest {

   private static final String SRC_TEST_RESOURCES_TESTDATA_SQL = "src/test/resources/testdata.sql";

   private static final String SRC_TEST_RESOURCES_SCHEMA_SQL = "src/test/resources/schema.sql";

   @Autowired
   private JobLauncherTestUtils jobLauncherTestUtils;

   private boolean first = true;

   @Before
   public void init() throws Exception {

      if (first) {
         try {
            System.out.println("executing sql script " + SRC_TEST_RESOURCES_SCHEMA_SQL);
            executeSqlScript(SRC_TEST_RESOURCES_SCHEMA_SQL);
            executeSqlScript(SRC_TEST_RESOURCES_TESTDATA_SQL);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      first = false;
   }

   @Test
   public void testJob() throws Exception {

      commonAssertions(jobLauncherTestUtils.launchJob());
   }

   @Test
   public void testStep1() throws Exception {

      commonAssertions(jobLauncherTestUtils.launchStep("step1"));
   }

   private void commonAssertions(JobExecution jobExecution) {

      assertNotNull(jobExecution);

      BatchStatus batchStatus = jobExecution.getStatus();
      assertEquals(BatchStatus.COMPLETED, batchStatus);
      assertFalse(batchStatus.isUnsuccessful());

      ExitStatus exitStatus = jobExecution.getExitStatus();
      assertEquals("COMPLETED", exitStatus.getExitCode());
      assertEquals("", exitStatus.getExitDescription());

      List<Throwable> failureExceptions = jobExecution.getFailureExceptions();
      assertNotNull(failureExceptions);
      assertTrue(failureExceptions.isEmpty());
   }

   private void executeSqlScript(String sqlScriptFilePath) throws Exception {

      List<String> sqlList = FileUtils.readLines(new File(sqlScriptFilePath), "UTF-8");

      for (String sql : sqlList) {
         jdbcTemplate.execute(sql);
      }
   }
}
