package galaxyoyo.minecraft.bukkit.jobs;

import galaxyoyo.minecraft.bukkit.jobs.skills.Skill;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class SkillTypeAdapter extends TypeAdapter<Skill>
{
	@Override
	public Skill read(JsonReader r) throws IOException
	{
		r.beginObject();
		r.nextName();
		String id = r.nextString();
		r.nextName();
		int cooldown = r.nextInt();
		r.endObject();
		
		Skill skill = null;
		for (Job job : Job.values())
		{
			skill = job.getActiveSkill(id);
			if (skill == null)
				skill = job.getPassiveSkill(id);
			if (skill != null)
				break;
		}
		if (skill == null)
			return null;
		skill = skill.clone();
		skill.setCooldown(cooldown);
		if (cooldown > 0)
			skill.startCooldown();
		return skill;
	}

	@Override
	public void write(JsonWriter w, Skill skill) throws IOException
	{
		w.beginObject();
		w.name("id");
		w.value(skill.getId());
		w.name("cooldown");
		w.value(skill.getCooldown());
		w.endObject();
	}
}
