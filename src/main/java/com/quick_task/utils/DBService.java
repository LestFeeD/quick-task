package com.quick_task.utils;

import com.quick_task.entity.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.spi.ServiceException;

public abstract class DBService {
    private static SessionFactory sessionFactory;

    static {
        try {
            Configuration configuration = getConfiguration();
            sessionFactory = createSessionFactory(configuration);
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }

    private DBService() {
    }

    private static Configuration getConfiguration() {
        Configuration configuration = new Configuration().configure();
        configuration.addAnnotatedClass(CommentProject.class);
        configuration.addAnnotatedClass(CommentTask.class);
        configuration.addAnnotatedClass(Priority.class);
        configuration.addAnnotatedClass(Project.class);
        configuration.addAnnotatedClass(RoleProjectTask.class);
        configuration.addAnnotatedClass(RoleUserProject.class);
        configuration.addAnnotatedClass(RoleUserTask.class);
        configuration.addAnnotatedClass(Status.class);
        configuration.addAnnotatedClass(StatusProject.class);
        configuration.addAnnotatedClass(StatusTask.class);
        configuration.addAnnotatedClass(Tag.class);
        configuration.addAnnotatedClass(Task.class);
        configuration.addAnnotatedClass(WebUser.class);
        configuration.addAnnotatedClass(StatusRole.class);
        configuration.addAnnotatedClass(TaskParticipants.class);
        configuration.addAnnotatedClass(ProjectParticipants.class);
        configuration.addAnnotatedClass(ConfirmationToken.class);
        return configuration;
    }

    private static SessionFactory createSessionFactory(Configuration configuration) {
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
        builder.applySettings(configuration.getProperties());
        ServiceRegistry serviceRegistry = builder.build();
        return configuration.buildSessionFactory(serviceRegistry);
    }

    public static Transaction getTransaction() {
        Session session = DBService.getSessionFactory().getCurrentSession();
        Transaction transaction = session.getTransaction();
        if (!transaction.isActive()) {
            transaction = session.beginTransaction();
        }
        return transaction;
    }


    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = getConfiguration();
                sessionFactory = createSessionFactory(configuration);
            } catch (Exception e) {
                System.out.println("Exception when creating a SessionFactory!" + e);
            }
        }
        return sessionFactory;
    }


    }

