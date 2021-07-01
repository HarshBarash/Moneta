package harshbarash.github.monetaru;

import android.net.Uri;
import android.provider.BaseColumns;


//сущность монета
public final class Moneta {

    public static final String CONTENT_AUTHORITY = "harshbarash.github.monetaru";
    public static final Uri BASE_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_COINS = "mycoins";
    public Moneta() {
    }

    public static abstract class CoinEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, PATH_COINS);

        public static final String TABLE_NAME = "mycoins";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NOMINAL = "nominal";
        public static final String COLUMN_CONDITION = "condition";
        public static final String COLUMN_MAGNET = "magnet";
        public static final String COLUMN_STAMP = "stamp";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_MATERIAL = "material";
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_DESCRIPTION = "description";


        public static final String TYPEOFNOMINAL_1RUBEL = "1 Рубль";
        public static final String TYPEOFNOMINAL_2RUBEL = "2 Рубля";
        public static final String TYPEOFNOMINAL_5RUBEL = "5 Рублей";
        public static final String TYPEOFNOMINAL_10RUBEL = "10 Рублей";

        public static final String TYPEOFCONDITION_1 = "1 - PR";
        public static final String TYPEOFCONDITION_2 = "2 - G";
        public static final String TYPEOFCONDITION_3 = "3 - VG";
        public static final String TYPEOFCONDITION_4 = "4 - F";
        public static final String TYPEOFCONDITION_5 = "5 - VF";
        public static final String TYPEOFCONDITION_6 = "6 - XF";
        public static final String TYPEOFCONDITION_7 = "7 - UNC";

        public static final String TYPEOFMAGNET_DA = "Да";
        public static final String TYPEOFMAGNET_NET = "Нет";

        public static final String TYPEOFSTAMP_M = "ШТ.М(А)";
        public static final String TYPEOFSTAMP_S = "ШТ.СП";

        public static final String TYPEOFTYPE_OB = "В обращении";
        public static final String TYPEOFTYPE_YB = "Юбилейная";
        public static final String TYPEOFTYPE_IN = "Инвестиционная";

        public static final String TYPEOFMATERIAL_WHITE = "Белый металл";
        public static final String TYPEOFMATERIAL_YELLOW = "Желтый металл";
        public static final String TYPEOFMATERIAL_OTHER = "Другой";


        public static boolean isValidType(String type) {
            if (type == TYPEOFNOMINAL_1RUBEL || type == TYPEOFNOMINAL_2RUBEL || type == TYPEOFNOMINAL_5RUBEL || type == TYPEOFNOMINAL_10RUBEL) {
                return true;
            }
            return false;
        }

        public static boolean isValidType2(String type) {
            if (type == TYPEOFCONDITION_1 || type == TYPEOFCONDITION_2 || type == TYPEOFCONDITION_3 || type == TYPEOFCONDITION_4
                    || type == TYPEOFCONDITION_5 || type == TYPEOFCONDITION_6 || type == TYPEOFCONDITION_7) {
                return true;
            }
            return false;
        }

        public static boolean isValidType3(String type) {
            if (type == TYPEOFMAGNET_DA || type == TYPEOFMAGNET_NET) {
                return true;
            }
            return false;
        }

        public static boolean isValidType4(String type) {
            if (type == TYPEOFSTAMP_M || type == TYPEOFSTAMP_S) {
                return true;
            }
            return false;
        }

        public static boolean isValidType5(String type) {
            if (type == TYPEOFTYPE_OB || type == TYPEOFTYPE_YB || type == TYPEOFTYPE_IN) {
                return true;
            }
            return false;
        }

        public static boolean isValidType6(String type) {
            if (type == TYPEOFMATERIAL_WHITE || type == TYPEOFMATERIAL_YELLOW || type == TYPEOFMATERIAL_OTHER) {
                return true;
            }
            return false;
        }


    }


}





