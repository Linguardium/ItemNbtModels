package mod.linguardium.itemnbtmodels.compat;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import mod.linguardium.itemnbtmodels.Main;
import mod.linguardium.itemnbtmodels.api.ModelOverrideNBTList;
import mod.linguardium.itemnbtmodels.api.NbtMatcher;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelVariantProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.client.render.model.json.ModelOverride;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;

import static net.minecraft.client.render.model.ModelLoader.MISSING;

@Environment(EnvType.CLIENT)
public class CITParser {
    public static void LoadPropertiesFiles(ModelLoader loader) throws IOException {
        Main.log(Level.INFO,"Loading properties files from optifine/cit/");
        ResourceManager manager= MinecraftClient.getInstance().getResourceManager();
        List<Identifier> models = Lists.newArrayList(manager.findResources("optifine/cit",
                (string) -> string.endsWith(".properties")));
        for(Identifier id : models) {
           Properties p = new Properties();
           p.load(manager.getResource(id).getInputStream());
           if (p.containsKey("type") && p.get("type").equals("item")) {
               List<String> evals = Lists.newArrayList();
               String model="";
               String texture="";
               if (p.containsKey("texture")) {
                   Main.log(Level.WARN,id.toString()+" contains custom CIT textures which are not currently supported. Move textures into a custom model.");
               }
               if (p.containsKey("model")) {
                   model = (String) p.get("model");
                   if (!model.contains(":") && !model.startsWith("optifine/cit/")) {
                       model = Paths.get(id.getPath(),"..",model).normalize().toString().replace(File.separatorChar,'/');
                   }
               }

               p.forEach((k,v)->{
                   if (k instanceof String) {
                       String sk = (String)k;
                       if (sk.startsWith("nbt")) {
                           String eval = processNbt(sk.substring(sk.indexOf('.',0)+1),v);
                           if (!eval.isEmpty())
                               evals.add(eval);
                       }else if(sk.equals("damage")) {
                           evals.add("Nbt.Damage()=="+String.valueOf(v));
                       }else if(sk.equals("enchantmentIDs") && p.containsKey("enchantmentLevels")) {
                           List<String> enchantments = Arrays.asList(((String) v).split(" "));
                           List<String> levels = Arrays.asList(((String) p.get("enchantmentLevels")).split(" "));
                           for (int i =0;i<enchantments.size() && i<levels.size();i++){
                               String[] levelRange = levels.get(i).split("-");
                               String eval = "Nbt.Enchantment('"+enchantments.get(i)+"') >= "+levelRange[0];
                               if (levelRange.length>1) {
                                   eval+= " && Nbt.Enchantment('"+enchantments.get(i)+"') <= "+levelRange[1];
                               }
                               evals.add(eval);
                           }
                       }
                   }
               });
               String eval = "";
               for (String s : evals) {
                   eval += "&& "+s;
               }
               eval = eval.substring(3);
               Set<Identifier> items = Sets.newHashSet();
               if (p.containsKey("matchItems")) {
                   for(String s: ((String)p.get("matchItems")).split(" ")) {
                       Identifier itemId = new Identifier(s);
                       items.add(itemId);
                   }
               }
               if (p.containsKey("items")) {
                   for (String s : ((String) p.get("items")).split(" ")) {
                       Identifier itemId = new Identifier(s);
                       items.add(itemId);
                   }
               }
               if (model.isEmpty())
                   model=id.toString().substring(0,id.toString().lastIndexOf('.'));
               for (Identifier itemId : items) {
                       ModelOverride mo = new ModelOverride(new Identifier(model),Collections.emptyMap());
                       if (mo instanceof ModelOverrideNBTList) {
                           ((ModelOverrideNBTList) mo).setNbtPredicates(Lists.newArrayList(new NbtMatcher.NbtCheckValue(eval)));
                           UnbakedModel m = loader.getOrLoadModel(new ModelIdentifier(itemId,"inventory"));
                           if (m instanceof JsonUnbakedModel && !((JsonUnbakedModel) m).id.equals(MISSING.toString())) {
                               ((JsonUnbakedModel) m).getOverrides().add(mo);
                               //Main.log(Level.INFO,"Loaded \nEval: "+eval+"\nModel: "+model+"\n to item: "+itemId.toString());
                           }else{
                               Main.log(Level.WARN,"Unable to apply "+id.toString()+" to item: "+itemId.toString());
                           }
                       }
               }
           }else{
               Main.log(Level.ERROR,"Failed to load "+id.toString()+" due to incompatible or missing type property.");
           }
        }
        Main.log(Level.INFO,"Finished loading properties files.");


    }
    private static String processNbt(String path, Object value) {
        if (value instanceof String) {
            String s = (String)value;
            String regex="";
            String searchType = "g";
            if (s.startsWith("pattern:")) {
                regex = s.substring(s.indexOf(':',0)+1);
                regex = regex.replaceAll(".", "[$0]").replace("[*]", ".*");  // group all characters then replace * with .*
            }else if (s.startsWith("ipattern:")) {
                regex = s.substring(s.indexOf(':',0)+1);
                regex = regex.replaceAll(".", "[$0]").replace("[*]", ".*");  // group all characters then replace * with .*
                searchType = "ig";
            }else if (s.startsWith("regex:")) {
                regex = s.substring(s.indexOf(':',0)+1);
                searchType = "g";
            }else if (s.startsWith("iregex:")){
                regex = s.substring(s.indexOf(':',0)+1);
                searchType = "ig";
            }else {
                regex = s.replaceAll(".", "[$0]");
                searchType = "g";
            }
            return "new RegExp('"+regex+"', '"+searchType+"').test(Nbt.String('"+path+"'))";
        }else if (value instanceof Integer || value instanceof Short || value instanceof Long) {
            return "Nbt.Number('"+path+"')=="+String.valueOf(value);
        }else if (value instanceof Float || value instanceof Double) {
            return "Nbt.Decimal('"+path+"')=="+String.valueOf(value);
        }
        return "";
    }
    public static void applyPropertiesFiles() {

    }
    public static void ModelLoaderListener(ResourceManager manager, Consumer<ModelIdentifier> processor) {
        List<Identifier> models = Lists.newArrayList(manager.findResources("optifine/cit",
                (string) -> string.endsWith(".json")));
                for(Identifier id : models) {
                    ModelIdentifier modelId = new ModelIdentifier(new Identifier(id.getNamespace(),id.getPath().replace(".json","")),"");
                    processor.accept(modelId);
                }
    }
    public static class CITVariantProvider implements ModelVariantProvider {
        private final ResourceManager manager;
        public CITVariantProvider(ResourceManager resourceManager) {
            this.manager=resourceManager;
        }

        @Override
        public UnbakedModel loadModelVariant(ModelIdentifier mId, ModelProviderContext context) throws ModelProviderException {
            if (mId.getPath().contains("/cit/")) {
                Resource resource = null;
                Identifier file = new Identifier(mId.getNamespace(),mId.getPath()+".json");
                InputStreamReader reader=null;
                try {
                    resource = manager.getResource(file);
                    reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
                    return ((CITUnbakedModelBuilder)JsonUnbakedModel.deserialize(reader)).getCITUnbakedModel(mId);
                } catch (IOException e) {
                    Main.log(Level.ERROR,"Unable to load model json: "+mId);
                }finally {
                    IOUtils.closeQuietly(reader);
                    IOUtils.closeQuietly(resource);
                }
            }
            return null;
        }
    }
    static class CITPredicate {
        NbtMatcher.NbtCheckValue checkValue;
        Identifier model;
        List<Identifier> items;
        CITPredicate(Identifier model, NbtMatcher.NbtCheckValue value, List<Identifier> items) {
            this.model=model;
            this.items=items;
            this.checkValue=value;
        }
    }
}
