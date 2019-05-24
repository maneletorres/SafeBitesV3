package com.maneletorres.safebites.utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.maneletorres.safebites.R;
import com.maneletorres.safebites.entities.Nutrient;
import com.maneletorres.safebites.entities.Product;
import com.maneletorres.safebites.entities.ProductNotFormatted;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class Utils {
    public static final String TWO_PANE = "com.maneletorres.safebites.extras.TWO_PANE";
    public static final String CLASS_NAME = "com.maneletorres.safebites.extras.CLASS_NAME";
    public static final String TOAST_MESSAGE = "com.maneletorres.safebites.extras.TOAST_MESSAGE";
    public static final String PRODUCT = "com.maneletorres.safebites.extras.PRODUCT";
    public static final String PRODUCT_A = "com.maneletorres.safebites.extras.PRODUCT_A";
    public static final String PRODUCT_B = "com.maneletorres.safebites.extras.PRODUCT_B";
    public static final String INGREDIENTS = "com.maneletorres.safebites.extras.INGREDIENTS";
    public static final String INGREDIENTS_A = "com.maneletorres.safebites.extras.INGREDIENTS_A";
    public static final String INGREDIENTS_B = "com.maneletorres.safebites.extras.INGREDIENTS_B";
    public static final String IMAGE_RESOURCE_A = "com.maneletorres.safebites.extras.IMAGE_RESOURCE_A";
    public static final String IMAGE_RESOURCE_B = "com.maneletorres.safebites.extras.IMAGE_RESOURCE_B";
    public static final int RC_SCAN = 0x0000b90f;
    public static final int RC_SCAN_OPTION_1_FIRST_EXECUTION = 11;
    public static final int RC_SCAN_OPTION_1_SECOND_EXECUTION = 12;
    public static final int RC_SCAN_OPTION_2 = 2;

    public static Product formatProduct(Context context, ProductNotFormatted product) {
        try {
            String[] nutrients_name = {
                    // Ordered nutrients:
                    context.getString(R.string.energy), context.getString(R.string.fat), context.getString(R.string.saturated_fat), context.getString(R.string.monounsaturated_fat), context.getString(R.string.polyunsaturated_fat),
                    context.getString(R.string.carbohydrate), context.getString(R.string.sugars), context.getString(R.string.polyols), context.getString(R.string.starch), context.getString(R.string.fiber),
                    context.getString(R.string.proteins), context.getString(R.string.salt), context.getString(R.string.vitamin_a), context.getString(R.string.vitamin_d), context.getString(R.string.vitamin_e),
                    context.getString(R.string.vitamin_k), context.getString(R.string.vitamin_c), context.getString(R.string.vitamin_b1), context.getString(R.string.vitamin_b2), context.getString(R.string.vitamin_b3),
                    context.getString(R.string.vitamin_b6), context.getString(R.string.vitamin_b9), context.getString(R.string.vitamin_b12), context.getString(R.string.biotin), context.getString(R.string.pantothenic_acid),
                    context.getString(R.string.potassium), context.getString(R.string.chloride), context.getString(R.string.calcium), context.getString(R.string.phosphorus), context.getString(R.string.magnesium),
                    context.getString(R.string.iron), context.getString(R.string.zinc), context.getString(R.string.copper), context.getString(R.string.manganese), context.getString(R.string.fluoride),
                    context.getString(R.string.selenium), context.getString(R.string.chromium), context.getString(R.string.molybdenum), context.getString(R.string.iodine),

                    // Disordered nutrients:
                    context.getString(R.string.sodium), context.getString(R.string.casein), context.getString(R.string.serum_proteins), context.getString(R.string.nucleotides), context.getString(R.string.sucrose),
                    context.getString(R.string.glucose), context.getString(R.string.fructose), context.getString(R.string.lactose), context.getString(R.string.maltose), context.getString(R.string.maltodextrins),
                    context.getString(R.string.butyric_acid), context.getString(R.string.capric_acid), context.getString(R.string.caprylic_acid), context.getString(R.string.capric_acid),
                    context.getString(R.string.lauric_acid), context.getString(R.string.myristic_acid), context.getString(R.string.palmitic_acid), context.getString(R.string.stearic_acid),
                    context.getString(R.string.arachidic_acid), context.getString(R.string.behenic_acid), context.getString(R.string.lignoceric_acid), context.getString(R.string.cerotic_acid),
                    context.getString(R.string.montanic_acid), context.getString(R.string.melissic_acid), context.getString(R.string.omega_3_fat), context.getString(R.string.alpha_linolenic_acid),
                    context.getString(R.string.eicosapentaenoic_acid), context.getString(R.string.docosahexaenoic_acid), context.getString(R.string.omega_6_fat),
                    context.getString(R.string.linoleic_acid), context.getString(R.string.arachidonic_acid), context.getString(R.string.gamma_linolenic_acid),
                    context.getString(R.string.dihomo_gamma_linolenic_acid), context.getString(R.string.omega_9_fat), context.getString(R.string.oleic_acid), context.getString(R.string.elaidic_acid),
                    context.getString(R.string.gondoic_acid), context.getString(R.string.mead_acid), context.getString(R.string.erucic_acid), context.getString(R.string.nervonic_acid), context.getString(R.string.trans_fat),
                    context.getString(R.string.cholesterol), context.getString(R.string.alcohol), context.getString(R.string.silica), context.getString(R.string.bicarbonate), context.getString(R.string.caffeine), context.getString(R.string.taurine), context.getString(R.string.ph)
            };

            String[] JSONElements_100g = {
                    // Ordered nutrients:
                    "energy_value", "fat_value", "saturated-fat_value", "monounsaturated-fat_value", "polyunsaturated-fat_value",
                    "carbohydrates_value", "sugars_value", "polyols_value", "starch_value", "fiber_value",
                    "proteins_value", "salt_value", "vitamin-a_value", "vitamin-d_value", "vitamin-e_value",
                    "vitamin-k_value", "vitamin-c_value", "vitamin-b1_value", "vitamin-b2_value", "vitamin-pp_value",
                    "vitamin-b6_value", "vitamin-b9_value", "vitamin-b12_value", "biotin_value", "pantothenic-acid_value",
                    "potassium_value", "chloride_value", "calcium_value", "phosphorus_value", "magnesium_value",
                    "iron_value", "zinc_value", "copper_value", "manganese_value", "fluoride_value",
                    "selenium_value", "chromium_value", "molybdenum_value", "iodine_value",

                    // Disordered nutrients:
                    "sodium_value", "casein_value", "serum-proteins_value", "nucleotides_value", "sucrose_value",
                    "glucose_value", "fructose_value", "lactose_value", "maltose_value", "maltodextrins_value",
                    "butyric-acid_value", "caproic-acid_value", "caprylic-acid_value", "capric-acid_value",
                    "lauric-acid_value", "myristic-acid_value", "palmitic-acid_value", "stearic-acid_value",
                    "arachidic-acid_value", "behenic-acid_value", "lignoceric-acid_value", "cerotic-acid_value",
                    "montanic-acid_value", "melissic-acid_value", "omega-3-fat_value", "alpha-linolenic-acid_value",
                    "eicosapentaenoic-acid_value", "docosahexaenoic-acid_value", "omega-6-fat_value",
                    "linoleic-acid_value", "arachidonic-acid_value", "gamma-linolenic-acid_value",
                    "dihomo-gamma-linolenic-acid_value", "omega-9-fat_value", "oleic-acid_value", "elaidic-acid_value",
                    "gondoic-acid_value", "mead-acid_value", "erucic-acid_value", "nervonic-acid_value", "trans-fat_value",
                    "cholesterol_value", "alcohol_value", "silica_value", "bicarbonate_value",
                    "caffeine_value", "taurine_value", "ph_value"
            };

            String[] JSONElements_portion = {
                    // Ordered nutrients:
                    "energy_serving", "fat_serving", "saturated-fat_serving", "monounsaturated-fat_serving", "polyunsaturated-fat_serving",
                    "carbohydrates_serving", "sugars_serving", "polyols_serving", "starch_serving", "fiber_serving",
                    "proteins_serving", "salt_serving", "vitamin-a_serving", "vitamin-d_serving", "vitamin-e_serving",
                    "vitamin-k_serving", "vitamin-c_serving", "vitamin-b1_serving", "vitamin-b2_serving", "vitamin-pp_serving",
                    "vitamin-b6_serving", "vitamin-b9_serving", "vitamin-b12_serving", "biotin_serving", "pantothenic-acid_serving",
                    "potassium_serving", "chloride_serving", "calcium_serving", "phosphorus_serving", "magnesium_serving",
                    "iron_serving", "zinc_serving", "copper_serving", "manganese_serving", "fluoride_serving",
                    "selenium_serving", "chromium_serving", "molybdenum_serving", "iodine_serving",

                    // Disordered nutrients:
                    "sodium_serving", "casein_serving", "serum-proteins_serving", "nucleotides_serving", "sucrose_serving",
                    "glucose_serving", "fructose_serving", "lactose_serving", "maltose_serving", "maltodextrins_serving",
                    "butyric-acid_serving", "caproic-acid_serving", "caprylic-acid_serving", "capric-acid_serving",
                    "lauric-acid_serving", "myristic-acid_serving", "palmitic-acid_serving", "stearic-acid_serving",
                    "arachidic-acid_serving", "behenic-acid_serving", "lignoceric-acid_serving", "cerotic-acid_serving",
                    "montanic-acid_serving", "melissic-acid_serving", "omega-3-fat_serving", "alpha-linolenic-acid_serving",
                    "eicosapentaenoic-acid_serving", "docosahexaenoic-acid_serving", "omega-6-fat_serving",
                    "linoleic-acid_serving", "arachidonic-acid_serving", "gamma-linolenic-acid_serving",
                    "dihomo-gamma-linolenic-acid_serving", "omega-9-fat_serving", "oleic-acid_serving", "elaidic-acid_serving",
                    "gondoic-acid_serving", "mead-acid_serving", "erucic-acid_serving", "nervonic-acid_serving", "trans-fat_serving",
                    "cholesterol_serving", "alcohol_serving", "silica_serving", "bicarbonate_serving", "caffeine_serving", "taurine_serving", "ph_serving"
            };

            String[] JSONElements_unit = {
                    // Ordered nutrients:
                    "energy_unit", "fat_unit", "saturated-fat_unit", "monounsaturated-fat_unit", "polyunsaturated-fat_unit",
                    "carbohydrates_unit", "sugars_unit", "polyols_unit", "starch_unit", "fiber_unit",
                    "proteins_unit", "salt_unit", "vitamin-a_unit", "vitamin-d_unit", "vitamin-e_unit",
                    "vitamin-k_unit", "vitamin-c_unit", "vitamin-b1_unit", "vitamin-b2_unit", "vitamin-pp_unit",
                    "vitamin-b6_unit", "vitamin-b9_unit", "vitamin-b12_unit", "biotin_unit", "pantothenic-acid_unit",
                    "potassium_unit", "chloride_unit", "calcium_unit", "phosphorus_unit", "magnesium_unit",
                    "iron_unit", "zinc_unit", "copper_unit", "manganese_unit", "fluoride_unit",
                    "selenium_unit", "chromium_unit", "molybdenum_unit", "iodine_unit",

                    // Disordered nutrients:
                    "sodium_unit", "casein_unit", "serum-proteins_unit", "nucleotides_unit", "sucrose_unit",
                    "glucose_unit", "fructose_unit", "lactose_unit", "maltose_unit", "maltodextrins_unit",
                    "butyric-acid_unit", "caproic-acid_unit", "caprylic-acid_unit", "capric-acid_unit",
                    "lauric-acid_unit", "myristic-acid_unit", "palmitic-acid_unit", "stearic-acid_unit",
                    "arachidic-acid_unit", "behenic-acid_unit", "lignoceric-acid_unit", "cerotic-acid_unit",
                    "montanic-acid_unit", "melissic-acid_unit", "omega-3-fat_unit", "alpha-linolenic-acid_unit",
                    "eicosapentaenoic-acid_unit", "docosahexaenoic-acid_unit", "omega-6-fat_unit",
                    "linoleic-acid_unit", "arachidonic-acid_unit", "gamma-linolenic-acid_unit",
                    "dihomo-gamma-linolenic-acid_unit", "omega-9-fat_unit", "oleic-acid_unit", "elaidic-acid_unit",
                    "gondoic-acid_unit", "mead-acid_unit", "erucic-acid_unit", "nervonic-acid_unit", "trans-fat_unit",
                    "cholesterol_unit", "alcohol_unit", "silica_unit", "bicarbonate_unit", "caffeine_unit", "taurine_unit", "ph_unit"
            };

            // Default decimal separator modifier:
            DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
            otherSymbols.setDecimalSeparator('.');

            // 3 decimals formatter:
            DecimalFormat threeDecimalsFormat = new DecimalFormat("#.###", otherSymbols);
            threeDecimalsFormat.setRoundingMode(RoundingMode.CEILING);

            // 1 decimal formatter:
            DecimalFormat df = new DecimalFormat("#.#", otherSymbols);
            df.setRoundingMode(RoundingMode.CEILING);

            // Section I - Creation of nutrients:
            ArrayList<Nutrient> nutrients = new ArrayList<>();

            JsonObject nutrimentsJson = product.getNutriments();
            for (int i = 0; i < nutrients_name.length; i++) {
                // Nutrient per 100g:
                String current_nutrient_per100g;
                JsonElement jsonElement1 = nutrimentsJson.get(JSONElements_100g[i]);
                if (jsonElement1 != null) {
                    current_nutrient_per100g = formatChain(jsonElement1.toString());
                    if (current_nutrient_per100g.length() == 0) {
                        continue;
                    } else {
                        current_nutrient_per100g = threeDecimalsFormat.format(Double.parseDouble(current_nutrient_per100g));
                    }
                } else {
                    continue;
                }

                // Nutrient unit:
                String current_nutrient_unit = "";
                String aux = "";
                JsonElement jsonElement2 = nutrimentsJson.get(JSONElements_unit[i]);
                if (jsonElement2 != null) {
                    current_nutrient_unit = formatChain(jsonElement2.toString());
                    if (current_nutrient_unit.length() > 0 && i == 0) {
                        aux = current_nutrient_unit;
                        if (current_nutrient_unit.toUpperCase().equals("KCAL")) {
                            current_nutrient_per100g = df.format(Double.parseDouble(current_nutrient_per100g) * 4.184) + " kj / " + df.format(Double.parseDouble(current_nutrient_per100g));
                            current_nutrient_unit = current_nutrient_unit.toLowerCase();
                        } else if (current_nutrient_unit.toUpperCase().equals("KJ")) {
                            current_nutrient_per100g = df.format(Double.parseDouble(current_nutrient_per100g)) + " kJ / " + df.format(Double.parseDouble(current_nutrient_per100g) / 4.184);
                            current_nutrient_unit = " kcal";
                        }
                    }
                }

                // Nutrient per serving:
                int multiplier = 1;
                switch (current_nutrient_unit) {
                    case "mg":
                        multiplier = 1000;
                        break;
                    case "µg":
                        multiplier = 1000000;
                        break;
                    default:
                        break;
                }

                String current_nutrient_per_portion;
                JsonElement jsonElement3 = nutrimentsJson.get(JSONElements_portion[i]);
                if (jsonElement3 != null) {
                    current_nutrient_per_portion = formatChain(jsonElement3.toString());
                    if (current_nutrient_per_portion.length() == 0) {
                        current_nutrient_per_portion = "-";
                    } else {
                        if (i == 0) {
                            if (aux.toUpperCase().equals("KCAL")) {
                                current_nutrient_per_portion = df.format(Double.parseDouble(current_nutrient_per_portion) * 4.184 * multiplier) + " kj / " + df.format(Double.parseDouble(current_nutrient_per_portion));
                            } else if (aux.toUpperCase().equals("KJ")) {
                                current_nutrient_per_portion = df.format(Double.parseDouble(current_nutrient_per_portion)) + " kJ / " + df.format((Double.parseDouble(current_nutrient_per_portion) / 4.184) * multiplier);
                            }
                        } else {
                            current_nutrient_per_portion = threeDecimalsFormat.format(Double.parseDouble(current_nutrient_per_portion) * multiplier);
                        }
                    }
                } else {
                    current_nutrient_per_portion = "-";
                }

                nutrients.add(new Nutrient(nutrients_name[i], current_nutrient_per100g, current_nutrient_per_portion, current_nutrient_unit));
            }

            // Section II - Checking product attributes:
            String name = product.getProduct_name();
            if (name == null || name.length() == 0 || name.equals("?")) {
                name = "-";
            }

            String upc = product.getCode();
            if (upc == null || upc.length() == 0 || upc.equals("?")) {
                upc = "-";
            }

            String image_resource = product.getImage_small_url();
            if (image_resource == null || image_resource.length() == 0 || image_resource.equals("?")) {
                image_resource = "-";
            }

            String ingredients = product.getIngredients_text();
            if (ingredients == null || ingredients.length() == 0 || ingredients.equals("?")) {
                ingredients = "-";
            }

            String serving_size = product.getServing_size();
            if (serving_size == null || serving_size.length() == 0 || serving_size.equals("?") || serving_size.equals("0")) {
                serving_size = "-";
            }

            // Section III - Joint creation of allergens and traces:
            ArrayList<String> allergensAndTraces = new ArrayList<>();

            JsonArray allergens = product.getAllergens_hierarchy();
            JsonArray traces = product.getTraces_hierarchy();
            if (allergens.size() > 0 && traces.size() > 0) {
                allergens.addAll(traces);
            }

            if (allergens.size() > 0) {
                allergensAndTraces = allergensJointCreation(allergens.toString().substring(1, allergens.toString().length() - 1));
            } else if (traces.size() > 0) {
                allergensAndTraces = allergensJointCreation(traces.toString().substring(1, traces.toString().length() - 1));
            }

            return new Product(upc, name, image_resource, nutrients, ingredients, serving_size, allergensAndTraces);
        } catch (Exception ex) {
            return null;
        }
    }

    private static String formatChain(String chain) {
        String result = chain;
        if (chain.charAt(0) == '"') {
            String aux = chain.substring(1);
            char fin = aux.charAt(aux.length() - 1);
            if (fin == '"') {
                result = aux.substring(0, aux.length() - 1);
            } else {
                result = aux;
            }
        }

        return result;
    }

    private static ArrayList<String> allergensJointCreation(String allergens) {
        ArrayList<String> arrayList = new ArrayList<>();
        String[] allergensSplitted = allergens.split(",");
        String[] auxSplitted = new String[allergensSplitted.length];

        for (int i = 0; i < allergensSplitted.length; i++) {
            String auxAllergen = formatChain(allergensSplitted[i]);
            auxSplitted[i] = auxAllergen;
        }

        Collections.addAll(arrayList, auxSplitted);

        return arrayList;
    }

    public static void hideSoftKeyboard(Activity activity) {
        if (activity.getCurrentFocus() == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus()
                .getWindowToken(), 0);
    }
}