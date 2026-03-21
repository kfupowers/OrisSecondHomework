//package ru.kpfu.itis.shakirov.repository;
//
//import ru.kpfu.itis.shakirov.model.User;
//import org.hibernate.HibernateException;
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//@Repository
//public class UserRepositoryHibernate {
//
//    private final SessionFactory sessionFactory;
//
//    public UserRepositoryHibernate(SessionFactory sessionFactory) {
//        this.sessionFactory = sessionFactory;
//    }
//
//    @Transactional(readOnly = true)
//    public List<User> findAll() {
//        try {
//            Session session = sessionFactory.getCurrentSession();
//            return session.createQuery("SELECT u FROM User u", User.class)
//                    .getResultList();
//        } catch (HibernateException e) {
//            throw new RuntimeException("Failed to get users", e);
//        }
//    }
//}