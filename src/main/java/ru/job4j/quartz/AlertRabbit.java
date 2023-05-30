package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {

    private static Connection connection;

    public static void main(String[] args) {
        Properties properties = getProperties();
        try {
            String interval = properties.getProperty("rabbit.interval");
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("time", initConnection(getProperties()));
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(Integer.parseInt(interval))
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Properties getProperties() {
        Properties config = new Properties();
        try (InputStream in = AlertRabbit.class.getClassLoader().getResourceAsStream("rabbit.properties")) {
            config.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return config;
    }

    public static Timestamp initConnection(Properties config) throws SQLException {
        String url = config.getProperty("url");
        String username = config.getProperty("username");
        String password = config.getProperty("password");
        connection = DriverManager.getConnection(url, username, password);
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        createTable();
        return timestamp;
    }

    private static void createTable() {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE rabbit (id SERIAL PRIMARY KEY, created_date TIMESTAMP)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Rabbit implements Job {
        public Rabbit() {
        }

        @Override
        public void execute(JobExecutionContext context) {
            context.getJobDetail().getJobDataMap().get("time");
        }
    }
}