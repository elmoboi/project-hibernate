package com.game.repository;

import com.game.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;

@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {

    private final SessionFactory sessionFactory;
    Logger logger = Logger.getAnonymousLogger();

    public PlayerRepositoryDB() {
        Properties properties = new Properties();
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
        properties.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
        properties.put(Environment.URL, "jdbc:mysql://localhost:3306/rpg");
        properties.put(Environment.USER, "olejik");
        properties.put(Environment.PASS, "ZehIDy");
        properties.put(Environment.HBM2DDL_AUTO, "update");

        sessionFactory = new Configuration()
                .addAnnotatedClass(Player.class)
                .addProperties(properties)
                .buildSessionFactory();
    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        String hql = "select * from rpg.player";
        NativeQuery<Player> query = session.createNativeQuery(hql,Player.class);
        query.setFirstResult(pageNumber * pageSize);
        query.setMaxResults(pageSize);
        session.getTransaction().commit();
        return query.list();
    }

    @Override
    public int getAllCount() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Long count = session.createNamedQuery("getCountPlayers", Long.class).uniqueResult();
        session.getTransaction().commit();
        return Math.toIntExact(count);
    }

    @Override
    public Player save(Player player) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Player newPlayer = new Player();
        newPlayer.setId(player.getId());
        newPlayer.setName(player.getName());
        newPlayer.setTitle(player.getTitle());
        newPlayer.setRace(player.getRace());
        newPlayer.setProfession(player.getProfession());
        newPlayer.setBirthday(player.getBirthday());
        newPlayer.setBanned(player.getBanned());
        newPlayer.setLevel(player.getLevel());
        session.persist(newPlayer);
        session.getTransaction().commit();
        return newPlayer;
    }

    @Override
    public Player update(Player player) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Player newPlayer = new Player();
        newPlayer.setId(player.getId());
        newPlayer.setName(player.getName());
        newPlayer.setTitle(player.getTitle());
        newPlayer.setRace(player.getRace());
        newPlayer.setProfession(player.getProfession());
        newPlayer.setBirthday(player.getBirthday());
        newPlayer.setBanned(player.getBanned());
        newPlayer.setLevel(player.getLevel());
        session.update(newPlayer);
        session.getTransaction().commit();
        return newPlayer;
    }

    @Override
    public Optional<Player> findById(long id) {
        Session session = sessionFactory.openSession();
        Optional<Player> player = Optional.ofNullable(session.get(Player.class, id));
        return player;
    }

    @Override
    public void delete(Player player) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.delete(player);
        session.getTransaction().commit();
    }

    @PreDestroy
    public void beforeStop() {
        sessionFactory.close();
    }
}