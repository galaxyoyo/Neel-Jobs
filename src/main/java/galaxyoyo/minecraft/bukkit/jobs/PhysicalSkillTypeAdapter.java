package galaxyoyo.minecraft.bukkit.jobs;

import galaxyoyo.minecraft.bukkit.jobs.skills.PhysicalSkill;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class PhysicalSkillTypeAdapter extends TypeAdapter<PhysicalSkill>
{
	@Override
	public PhysicalSkill read(JsonReader r) throws IOException
	{
		r.beginObject();
		r.nextName();
		int id = r.nextInt();
		r.nextName();
		double points = r.nextDouble();
		r.nextName();
		boolean active = r.nextBoolean();
		r.nextName();
		boolean activated = r.nextBoolean();
		r.endObject();
		PhysicalSkill skill = (PhysicalSkill) PhysicalCompetences.getAvailableSkills().get(id).clone();
		skill.setPoints(points);
		skill.setActive(active);
		if (activated)
			skill.activate();
		else
			skill.desactivate();
		return skill;
	}

	@Override
	public void write(JsonWriter w, PhysicalSkill skill) throws IOException
	{
		w.beginObject();
		w.name("id");
		w.value(skill.getId());
		w.name("points");
		w.value(skill.getPoints());
		w.name("active");
		w.value(skill.isActive());
		w.name("activated");
		w.value(skill.isActivated());
		w.endObject();
	}
}
