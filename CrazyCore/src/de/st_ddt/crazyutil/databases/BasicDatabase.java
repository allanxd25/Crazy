package de.st_ddt.crazyutil.databases;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

public abstract class BasicDatabase<S extends DatabaseEntry> implements Database<S>
{

	protected final Map<String, S> datas = new HashMap<String, S>();
	private final DatabaseType type;
	private final Class<S> clazz;
	protected final Constructor<S> constructor;
	protected final String[] defaultColumnNames;

	public BasicDatabase(final DatabaseType type, final Class<S> clazz, final Constructor<S> constructor, final String[] defaultColumnNames)
	{
		super();
		this.type = type;
		this.clazz = clazz;
		this.constructor = constructor;
		this.defaultColumnNames = defaultColumnNames;
	}

	@Override
	public final DatabaseType getType()
	{
		return type;
	}

	@Override
	public Class<S> getEntryClazz()
	{
		return clazz;
	}

	@Override
	public abstract void initialize() throws Exception;

	@Override
	public void checkTable() throws Exception
	{
	}

	@Override
	public abstract boolean isStaticDatabase();

	@Override
	public abstract boolean isCachedDatabase();

	@Override
	public boolean hasEntry(final String key)
	{
		return datas.containsKey(key.toLowerCase());
	}

	@Override
	public final S getEntry(final String key)
	{
		return datas.get(key.toLowerCase());
	}

	@Override
	public final S getOrLoadEntry(final String key)
	{
		final S data = getEntry(key);
		if (isStaticDatabase())
			return data;
		else if (data == null)
			return loadEntry(key);
		else
			return data;
	}

	@Override
	public final Collection<S> getAllEntries()
	{
		return datas.values();
	}

	@Override
	public abstract S updateEntry(final String key);

	@Override
	public abstract S loadEntry(String key);

	@Override
	public abstract void loadAllEntries();

	@Override
	public boolean unloadEntry(final String key)
	{
		save(key);
		return datas.remove(key.toLowerCase()) != null;
	}

	@Override
	public void unloadAllEntries()
	{
		saveAll(getAllEntries());
		datas.clear();
	}

	@Override
	public void save(final String key)
	{
		final S entry = datas.get(key.toLowerCase());
		if (entry != null)
			save(entry);
	}

	@Override
	public void save(final S entry)
	{
		datas.put(entry.getName().toLowerCase(), entry);
	}

	@Override
	public void saveAll(final Collection<S> entries)
	{
		for (final S entry : entries)
			save(entry);
		saveDatabase();
	}

	@Override
	public boolean deleteEntry(final String key)
	{
		return datas.remove(key.toLowerCase()) != null;
	}

	@Override
	public void purgeDatabase()
	{
		datas.clear();
		saveDatabase();
	}

	@Override
	public abstract void saveDatabase();

	@Override
	public void save(final ConfigurationSection config, final String path)
	{
		config.set(path + "saveType", type.toString());
	}
}
