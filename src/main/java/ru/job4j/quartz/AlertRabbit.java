package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    public static void main(String[] args) {
        Properties properties = getProperties();
        try {
            String interval = properties.getProperty("rabbit.interval");
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            Connection connection = initConnection(getProperties());
            JobDataMap data = new JobDataMap();
            data.put("connection", connection);
            JobDetail job = newJob(Rabbit.class).usingJobData(data).build();
            SimpleScheduleBuilder times = simpleSchedule().withIntervalInSeconds(Integer.parseInt(interval)).repeatForever();
            Trigger trigger = newTrigger().startNow().withSchedule(times).build();
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

    public static Connection initConnection(Properties config) throws ClassNotFoundException, SQLException {
        Class.forName(config.getProperty("driver-class-name"));
        String url = config.getProperty("url");
        String username = config.getProperty("username");
        String password = config.getProperty("password");
        Connection connection = DriverManager.getConnection(url, username, password);
        createTable(connection);
        return connection;
    }

    private static void createTable(Connection connection) {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS rabbit (id SERIAL PRIMARY KEY, created_date TIMESTAMP)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Rabbit implements Job {
        public Rabbit() {
        }

        @Override
        public void execute(JobExecutionContext context) {
            try (Connection c = (Connection) context.getJobDetail().getJobDataMap().get("connection")) {
                Statement statement = c.createStatement();
                statement.execute("INSERT INTO rabbit (created_date) VALUES (CURRENT_TIMESTAMP)");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}