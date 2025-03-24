package com.quick_task.dao;

import com.quick_task.entity.Project;
import com.quick_task.entity.Task;
import com.quick_task.exception.EntityNotFoundException;
import com.quick_task.utils.DBService;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ProjectDAOImpl implements ProjectDAO, Dao{

     public ProjectDAOImpl() {
    }

    @Override
    public Project findById(Long id) throws SQLException {
        Session session = DBService.getSessionFactory().getCurrentSession();
        return Optional.ofNullable(session.get(Project.class, id))
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Project with ID \"%s\" not found.", id)
                ));
     }

    @Override
    public Project create(Project project) throws SQLException {
        Session session =  DBService.getSessionFactory().getCurrentSession();
        session.persist(project);
        return project;
    }

    @Override
    public List<Project> getAllByIdUser(Long idWebUser) throws SQLException {
        return DBService.getSessionFactory()
                .getCurrentSession()
                .createNativeQuery("SELECT p.* FROM project p " +
                        "JOIN project_participants pp ON pp.id_project = p.id_project " +
                        "WHERE pp.id_web_user = :id_web_user", Project.class)
                .setParameter("id_web_user", idWebUser)
                .list();
    }

    @Override
    public List<Project> findAllProjectsByStatus(Long userId, String nameStatus) throws SQLException {
        return DBService.getSessionFactory()
                .getCurrentSession()
                .createNativeQuery("WITH RECURSIVE ProjectChain AS (\n" +
                        "    SELECT sp.*, ARRAY[sp.id_status_project] AS visited\n" +
                        "    FROM status_project sp\n" +
                        "    JOIN project_participants pp ON sp.id_project = pp.id_project\n" +
                        "    JOIN status s ON s.id_status = sp.id_status\n" +
                        "    WHERE sp.id_left_project_status IS NULL\n" +
                        "    AND s.name_status = :name_status\n" +
                        "    AND pp.id_web_user = :id_web_user\n" +
                        "    UNION ALL\n" +
                        "    SELECT sp.*, pc.visited || sp.id_status_project AS visited\n" +
                        "    FROM status_project sp\n" +
                        "    INNER JOIN ProjectChain pc ON sp.id_left_project_status = pc.id_status_project  \n" +
                        "    WHERE sp.id_status_project != ALL(pc.visited)  \n" +
                        ")\n" +
                        "SELECT p.*\n" +
                        "FROM ProjectChain pc\n" +
                        "JOIN project p ON pc.id_project = p.id_project\n" +
                        "JOIN project_participants pp ON p.id_project = pp.id_project\n" +
                        "JOIN web_user u ON u.id_web_user = pp.id_web_user\n" +
                        "WHERE pp.id_web_user = :id_web_user\n" +
                        "ORDER BY pc.visited;  ", Project.class)
                .setParameter("name_status", nameStatus)
                .setParameter("id_web_user", userId)
                .list();
    }

    @Override
    public List<Project> findProjectGroupedByStatus(Long userId, String nameStatus) throws SQLException {
        return DBService.getSessionFactory()
                .getCurrentSession()
                .createNativeQuery( "SELECT t.* " +
                "FROM project t " +
                "JOIN status_project st ON t.id_project = st.id_project " +
                "JOIN status s ON st.id_status = s.id_status " +
                "JOIN project_participants tp ON t.id_project = tp.id_project " +
                "JOIN web_user wu ON wu.id_web_user = tp.id_web_user " +
                "WHERE tp.id_web_user = :userId AND " +
                "s.name_status = :name_status"
                , Project.class)
                .setParameter("userId", userId)
                .setParameter("name_status", nameStatus)
                .list();

    }

    @Override
    public List<Project> getByName(String parameter) throws SQLException {
        return DBService.getSessionFactory()
                .getCurrentSession()
                .createNativeQuery("SELECT * FROM project WHERE name_project LIKE :name", Project.class)
                .setParameter("name", parameter + "%")
                .list();
    }

    @Override
    public Project update(Project project) throws SQLException {
        DBService.getSessionFactory().getCurrentSession()
                .merge(project);
        return project;
    }

    @Override
    public void removeById(Long idProject) {
        Session session = DBService.getSessionFactory().getCurrentSession();
        Project project = session.getReference(Project.class, idProject);
        session.remove(project);
    }


}
