package fr.jburet.nav.database;

import java.io.Serializable;
import java.util.List;

import fr.jburet.nav.database.point.Waypoint;

public interface Query<T> {
	public void delete(T entity);

	public void deleteByPk(Serializable pk);

	public T selectByPk(Serializable pk);

	public List<T> listAll();

	public void deleteAll();

	public T save(T newEntity);

}
