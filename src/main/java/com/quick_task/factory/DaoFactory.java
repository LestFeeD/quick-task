package com.quick_task.factory;

import com.quick_task.dao.*;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public abstract class DaoFactory {

    private static final List<Dao> daoList = Arrays.asList(
            new ProjectDAOImpl(),
            new PriorityDAOImpl(),
            new WebUserDAOImpl(),
            new TaskDaoImpl(),
            new StatusDaoImpl(),
            new TaskDaoImpl(),
            new StatusTaskDAOImpl(),
            new TagDAOImpl(),
            new StatusRoleDAOImpl(),
            new TaskParticipantsDAOImpl(),
            new ProjectParticipantsDAOImpl(),
            new CommentTaskDAOImpl(),
            new StatusProjectDAOImpl(),
            new CommentProjectDAOImpl(),
            new ConfirmationTokenDAOImpl()

    );

    public static <T extends Dao> T getDao(Class<T> cl){
        for(Dao dao : daoList)
            if (cl.isInstance(dao))
                return cl.cast(dao);
        return null;
    }



}
