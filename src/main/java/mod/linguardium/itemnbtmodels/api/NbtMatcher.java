package mod.linguardium.itemnbtmodels.api;

import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import net.minecraft.command.arguments.NbtPathArgumentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.Tag;
import net.minecraft.text.Text;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.List;

public class NbtMatcher {
    private static ScriptEngine Engine = new NashornScriptEngineFactory().getScriptEngine();
    private static NbtPathArgumentType pathArg = NbtPathArgumentType.nbtPath();
    private static String ENCHANTMENT_PATH = "";
    //static {
    //    System.setProperty("polyglot.js.nashorn-compat", "true");
    //}
    static public class NbtCheckValue {
        String eval;
        public NbtCheckValue(String eval) {
            this.eval=eval;
        }
    }

    public static class StackMatcher {
        ItemStack stack;
        LivingEntity entity;
        StackMatcher(ItemStack itemStack, LivingEntity entity) {
            stack = itemStack; this.entity=entity;
        }
        public Long Number(String string) {
            Long l = 0L;
            try {
                l=Long.parseLong(path(string));
            }catch(NumberFormatException ignored) { }
            return l;
        }
        public Double Decimal(String string) {
            Double d = 0.0D;
            try {
                d=Double.parseDouble(path(string));
            }catch(NumberFormatException ignored) { }
            return d;

        }
        public String String(String string) {
            return path(string);
        }
        public String Text(String string) {
            Text t=null;
            try {
                t = Text.Serializer.fromJson(path(string));
            }catch (JsonParseException ignored) { }
            if (t!=null)
                return t.asString();
            return "";
        }
        public Integer Count() {
                return stack.getCount();
        }
        public Boolean MainHand() {
            return entity.getMainHandStack().equals(stack);
        }
        public Boolean OffHand() {
            return entity.getOffHandStack().equals(stack);
        }
        public Integer Damage() {
            return stack.getDamage();
        }
        public Integer MaxDamage() {
            return stack.getMaxDamage();
        }

        public String path(String string) {
            try {
                Tag ret = null;
                NbtPathArgumentType.NbtPath path= pathArg.parse(new StringReader(string));
                List<Tag> tag = path.get(stack.getOrCreateTag());
                if (tag.size()>0)
                    return tag.get(0).asString();
            } catch (CommandSyntaxException ignored) {}
            return "";
        }

    }
    public static boolean matches(ItemStack stack, LivingEntity entity, NbtCheckValue pred) {
        Bindings bindings = Engine.createBindings();
        bindings.put("Nbt",new StackMatcher(stack,entity));
        try {
            Object retval = Engine.eval(pred.eval,bindings);
            if (retval instanceof Boolean)
                return (Boolean)retval;
        }catch(ScriptException ignored) { }
        return false;
    }
}
