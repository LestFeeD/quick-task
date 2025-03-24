package com.quick_task.dao;

import com.quick_task.entity.Task;
import com.quick_task.utils.DBService;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaskDaoImpl implements TaskDAO, Dao{

    @Override
    public Task findById(Long id) throws SQLException {
        Session session = DBService.getSessionFactory().getCurrentSession();
        return session.get(Task.class, id);
    }

    @Override
    public Task create(Task task) throws SQLException {
        Session session =  DBService.getSessionFactory().getCurrentSession();
        session.persist(task);
        return task;
    }

    @Override
    public Task update(Task task) throws SQLException {
        DBService.getSessionFactory().getCurrentSession()
                .merge(task);
        return task;
    }

    @Override
    public List<Task> getAllByIdUser(Long idUser) throws SQLException {
        return DBService.getSessionFactory()
                .getCurrentSession()
                .createNativeQuery("SELECT t.* FROM task t " +
                        "JOIN task_participants tp ON tp.id_task = t.id_task " +
                        "WHERE   tp.id_web_user = :id_web_user", Task.class)
                .setParameter("id_web_user", idUser)
                .list();
    }

    @Override
    public List<Task> getAllTaskWithoutProjectByIdUser(Long idUser) throws SQLException {
        return DBService.getSessionFactory()
                .getCurrentSession()
                .createNativeQuery("SELECT t.* FROM task t " +
                        "JOIN  task_participants tp ON t.id_task = tp.id_task " +
                        "WHERE tp.id_web_user = :id_web_user", Task.class)
                .setParameter("id_web_user", idUser)
                .list();
    }

    @Override
    public List<Task> getByName(String parameter) throws SQLException {
        return DBService.getSessionFactory()
                .getCurrentSession()
                .createNativeQuery("SELECT * FROM task WHERE name_task LIKE :name", Task.class)
                .setParameter("name", parameter + "%")
                .list();
    }

    @Override
    public List<Task> getByProject(Long idProject) throws SQLException {
        return DBService.getSessionFactory()
                .getCurrentSession()
                .createNativeQuery("SELECT t.* FROM task t " +
                        "JOIN project p ON p.id_project = t.id_project " +
                        "WHERE p.id_project = :id_project", Task.class)
                .setParameter("id_project", idProject)
                .list();       }

    @Override
    public List<Task> findAllTasksByStatus(Long userId, String nameStatus) {
        return DBService.getSessionFactory()
                .getCurrentSession()
                .createNativeQuery("WITH RECURSIVE TaskChain AS (\n" +
                        "    SELECT st.*, ARRAY[st.id_status_task] AS visited\n" +
                        "    FROM status_task st\n" +
                        "    JOIN task_participants tp ON st.id_task = tp.id_task\n" +
                        "    JOIN status s ON s.id_status = st.id_status\n" +
                        "    WHERE st.id_left_task_status IS NULL\n" +
                        "      AND s.name_status = :name_status\n" +
                        "      AND tp.id_web_user = :id_web_user   \n" +
                        "\n" +
                        "    UNION ALL\n" +
                        "\n" +
                        "    SELECT st.*, tc.visited || st.id_status_task AS visited\n" +
                        "    FROM status_task st\n" +
                        "    INNER JOIN TaskChain tc ON st.id_status_task = tc.id_right_task_status\n" +
                        "    WHERE st.id_status_task != ALL(tc.visited)  \n" +
                        ")\n" +
                        "\n" +
                        "SELECT t.*\n" +
                        "FROM TaskChain tc\n" +
                        "JOIN task t ON tc.id_task = t.id_task\n" +
                        "JOIN task_participants tp ON tp.id_task = t.id_task\n" +
                        "JOIN web_user u ON u.id_web_user = tp.id_web_user\n" +
                        "WHERE tp.id_web_user = :id_web_user   \n" +
                        "ORDER BY tc.visited;  ", Task.class)
                .setParameter("name_status", nameStatus)
                .setParameter("id_web_user", userId)
                .list();

    }

    @Override
    public void deleteById(Long idTask) throws SQLException {
        Session session = DBService.getSessionFactory().getCurrentSession();
        Task task = session.getReference(Task.class, idTask);
        session.remove(task);
    }

    @Override
    public void deleteByUser(Long idUser) throws SQLException {
         DBService.getSessionFactory()
                .getCurrentSession()
                .createNativeQuery("DELETE FROM task t " +
                        "JOIN task_participants tp ON tp.id_task = t.id_task " +
                        "WHERE tp.id_web_user = :id_web_user")
                .setParameter("id_web_user", idUser)
                .executeUpdate();
    }

    @Override
    public List<Task> findSortedTasksWithFilters(Long userId, String nameStatus, Long idProject) throws SQLException {
        Session session = DBService.getSessionFactory().getCurrentSession();

        StringBuilder query = new StringBuilder(
                "SELECT t.* " +
                        "FROM task t " +
                        "JOIN status_task st ON t.id_task = st.id_task " +
                        "JOIN status s ON st.id_status = s.id_status " +
                        "JOIN status_role sr ON s.id_status_role = sr.id_status_role " +
                        "JOIN task_participants tp ON t.id_task = tp.id_task " +
                        "JOIN web_user wu ON wu.id_web_user = tp.id_web_user " +
                        "WHERE 1=1 ");
        if (userId != null) {
            query.append("AND tp.id_web_user = :userId ");
        }



        if (idProject != null) {
            query.append("AND t.id_project = :idProject ");
        }
        query.append(
                "ORDER BY CASE " +
                        "WHEN s.name_status = :nameStatus THEN 0 " +
                        "WHEN sr.id_status_role = (SELECT sr2.id_status_role FROM status_role sr2 " +
                        "JOIN status s2 ON s2.id_status_role = sr2.id_status_role " +
                        "WHERE s2.name_status = :nameStatus LIMIT 1) THEN 1 " +
                        "ELSE 2 END, " +
                        "sr.order_status_role, t.id_task"
        );
        NativeQuery<Task> nativeQuery = DBService.getSessionFactory()
                .getCurrentSession()
                .createNativeQuery(query.toString(), Task.class);

        if (userId != null) {
            nativeQuery.setParameter("userId", userId);
        }

        if (nameStatus != null && !nameStatus.isEmpty()) {
            nativeQuery.setParameter("nameStatus", nameStatus);
        }

        if (idProject != null) {
            nativeQuery.setParameter("idProject", idProject);
        }
        return nativeQuery.list();
    }


}
