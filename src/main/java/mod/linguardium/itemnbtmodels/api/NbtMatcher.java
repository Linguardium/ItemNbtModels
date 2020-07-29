package mod.linguardium.itemnbtmodels.api;

import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.arguments.NbtPathArgumentType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.Tag;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class NbtMatcher {
    private static Context context = Context.create("js");
    private static ScriptEngine Engine = new ScriptEngineManager().getEngineByName("graal.js");
    private static NbtPathArgumentType pathArg = NbtPathArgumentType.nbtPath();
    private static String ENCHANTMENT_PATH = "";
    static {
        System.setProperty("polyglot.js.nashorn-compat", "true");
    }
    static public class NbtCheckValue {
        String eval;
        NbtCheckValue(String eval) {
            this.eval=eval;
        }
    }

    public static class StackMatcher {
        ItemStack stack;
        StackMatcher(ItemStack itemStack) {
            stack = itemStack;
        }
        @HostAccess.Export
        public Long Number(String string) {
            Long l = 0L;
            try {
                l=Long.parseLong(path(string));
            }catch(NumberFormatException ignored) { }
            return l;
        }
        @HostAccess.Export
        public Double Decimal(String string) {
            Double d = 0.0D;
            try {
                d=Double.parseDouble(path(string));
            }catch(NumberFormatException ignored) { }
            return d;

        }
        @HostAccess.Export
        public String String(String string) {
            return path(string);
        }
        @HostAccess.Export
        public String Text(String string) {
            Text t=null;
            try {
                t = Text.Serializer.fromJson(path(string));
            }catch (JsonParseException ignored) { }
            if (t!=null)
                return t.asString();
            return "";
        }
        @HostAccess.Export
        public Integer Enchantment(String string) {
                return EnchantmentHelper.getLevel(Registry.ENCHANTMENT.get(new Identifier(string)),stack);
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
    public static boolean matches(ItemStack stack, NbtCheckValue pred) {
        Value bindings = context.getBindings("js");
        bindings.putMember("Nbt",new StackMatcher(stack.copy()));
        try {
            Value retval = context.eval("js", pred.eval);
            if (retval.isBoolean())
                return retval.asBoolean();
        }catch(PolyglotException ignored) { }
        return false;
    }
}
