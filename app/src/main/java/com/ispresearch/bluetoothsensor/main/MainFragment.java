package com.ispresearch.bluetoothsensor.main;

//Notes:
//400 hz sine wave
//.5 mill (2khz)

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ispresearch.bluetoothsensor.BluetoothSensor;
import com.ispresearch.bluetoothsensor.Manifest;
import com.ispresearch.bluetoothsensor.R;
import com.ispresearch.bluetoothsensor.alldataentities.AllDataEntitiesActivity;
import com.ispresearch.bluetoothsensor.data.DataEntityDatabase;
import com.ispresearch.bluetoothsensor.graph.GraphActivity;
import com.ispresearch.bluetoothsensor.savedata.SaveDataActivity;
import com.ispresearch.bluetoothsensor.viewmodel.DataEntityCollectionViewModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import static android.app.Activity.RESULT_OK;

public class MainFragment extends Fragment {


    // GUI Components
    private TextView mBluetoothStatus;
    private TextView mReadBuffer;
    private Button mOnBtn;
    private Button mOffBtn;
    private Button mListPairedDevicesBtn;
    private Button mDiscoverBtn;
    private BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<String> mBTArrayAdapter;
    private ListView mDevicesListView;
    private Button mSaveData;
    private Button mViewGraphBt;
    private Button mPrevData;
    private boolean zero = false;

    private View v;


    ArrayList<Double> data = new ArrayList<Double>();


//Early Systolic Heart Sound for Testing
//    ArrayList<Integer> dataTest = new ArrayList<>(Arrays.asList(23,-26,18,-9,-37,-2,-6,4,20,-34,1,-25,18,8,-13,4,7,3,-5,-0,-3,-2,-13,-3,-16,-4,-9,-2,-5,-1,6,16,-12,18,-23,27,40,10,-21,-52,13,39,-37,-4,-21,6,-24,-8,5,16,18,-35,38,4,46,-72,40,-2,-16,17,-54,49,-66,27,19,-12,45,-14,-31,21,11,21,-19,-40,54,-159,307,-546,2229,-684,3532,-4833,1576,816,-531,2005,-2412,2347,-552,450,1341,-36,707,-121,-1034,65,-498,171,-3,-846,883,-233,-704,-601,-1150,1294,485,-659,508,-503,522,909,920,6,976,-234,338,238,-339,-28,-107,77,49,-39,259,-120,265,7,10,74,334,266,56,58,-170,520,112,279,-11,-290,-14,-176,-135,-223,164,-232,-0,44,-407,-284,-846,1085,-629,2613,-2361,1024,22,-125,-5,-99,265,-6,-141,16,-36,33,74,-140,-6,-52,40,12,-7,-9,35,-30,-12,23,-8,7,-7,-27,-8,14,-24,17,1,-29,5,-6,-7,8,-8,-16,-45,-21,7,33,-38,-38,2,-14,23,-23,-3,10,25,-2,-38,14,30,-2,-23,10,22,-15,-6,-9,16,3,-1,-9,15,-2,-10,-8,-9,5,6,-16,6,-36,2,-35,13,-17,10,-16,4,14,-23,44,-25,-21,5,3,31,-71,28,15,-13,-4,-18,18,-44,-10,-11,3,21,-12,-2,-24,25,-36,15,6,15,2,-38,13,22,-29,10,-60,15,-31,45,-11,-27,49,-86,-466,1177,336,2,2209,-3537,1146,-1140,-57,405,-279,1429,-296,-100,734,-717,-238,-407,128,1446,410,1084,-55,457,-1211,732,-58,-609,316,-564,-492,275,367,-9,66,407,-578,-96,342,-758,77,334,-779,-221,183,29,487,61,168,287,-190,68,-184,-177,121,-83,227,-43,41,-91,52,84,10,-53,-161,-24,-7,-459,35,-452,65,-385,81,-1905,968,94,-853,1696,-1064,211,-222,393,-12,-166,-38,-63,68,72,-127,16,-79,10,36,-27,8,-16,-10,-27,2,-22,1,31,-41,-2,-18,23,10,-24,-33,-10,-6,1,9,-33,-26,34,3,3,-39,9,28,-19,-15,10,42,-29,-27,2,6,-27,-21,21,-13,36,-79,-26,-37,49,-20,-7,-19,-15,39,-41,29,-9,9,15,-25,3,-9,4,2,-2,20,-53,31,-16,2,-9,-18,20,-65,36,-19,-40,20,-20,-33,-22,25,-54,44,-33,-47,-19,-60,-20,-23,-29,7,-33,-14,8,-28,51,-1,1,7,5,30,-23,-21,15,8,21,-31,-29,-6,-13,69,-131,419,-525,811,-1106,1674,-4187,1465,-420,-769,1585,-2535,1770,-2585,-18,959,-547,241,-624,-1149,-463,-430,657,-286,158,-600,-654,779,-250,-102,-1015,800,544,287,-23,189,188,108,411,252,4,339,176,31,180,192,-84,-233,373,-19,180,-104,142,100,133,154,156,135,174,-167,33,23,-239,110,-138,40,100,170,-127,126,111,-169,173,-67,827,248,1615,-1930,3486,-3190,1129,-99,-31,-42,-157,143,-63,17,24,-114,54,14,-79,75,-20,-20,-31,37,-17,42,-40,-18,34,-20,-7,6,4,2,-56,13,24,-1,24,-44,47,12,-38,33,-15,63,-68,24,16,-49,-14,-64,28,-38,4,3,-51,15,-3,10,-18,27,-30,0,46,-22,64,-37,12,-16,-5,8,-29,21,-10,4,22,-7,-19,-9,44,-5,2,-17,22,-16,-51,-53,-14,26,-45,5,-7,10,34,-39,-5,-15,53,-18,-28,-11,7,19,-22,-6,-13,22,-6,-3,3,-69,7,-55,18,-4,9,-18,20,2,49,1,35,-38,16,56,-75,79,-60,75,-61,437,-1510,3335,-4428,3535,-753,-893,3843,-4888,2767,-2767,2187,975,-105,1756,-1425,332,795,-622,885,-880,844,-382,-29,1299,349,28,214,1269,-645,-750,-288,-667,574,-965,505,472,194,1093,567,-522,-260,374,-519,-183,-73,123,-264,30,-36,-106,83,-49,-292,143,-49,-52,518,80,57,201,-233,204,226,-74,303,179,-126,66,-208,164,268,-331,1739,-712,1515,-1777,3392,-2515,497,358,34,-22,-274,75,80,36,-63,-188,76,39,-34,-101,1,59,-87,4,-32,76,-44,-17,-10,21,1,-57,7,-9,-33,-3,-2,40,38,-55,-19,-16,13,-5,26,-63,7,-9,-21,-27,1,5,-27,2,5,32,12,-16,13,-20,4,-29,6,-4,2,-10,26,27,-40,-10,12,6,28,-29,-8,-80,43,1,-8,-0,17,-32,13,16,-13,-7,-16,17,-3,-11,-47,51,-49,3,13,-52,-4,-23,-6,7,-20,-15,-29,-30,-13,18,-16,20,-67,30,3,-1,56,-45,12,5,22,29,-33,-5,0,55,-90,42,-13,16,-61,8,-44,63,-289,2368,-2198,3126,-4901,1822,322,-710,2785,-4565,4328,-2618,1337,2314,1227,1682,-70,1045,-672,509,-523,-838,491,-76,805,-80,983,969,566,1251,64,843,-260,1081,542,-583,186,1235,-66,-30,-131,588,-572,-468,-749,294,-885,-152,880,-315,-383,80,-81,-103,81,-119,-150,-806,-779,-5,111,-216,-153,29,29,-99,323,103,30,-24,-147,-46,803,-1674,2330,-616,3977,-4793,2286,-271,-505,146,103,298,-443,6,55,-96,65,26,-103,25,-89,-15,20,-13,-1,2,-63,7,-2,19,-23,-38,-51,25,-55,-11,11,-11,61,-90,-5,7,24,-24,3,-42,10,-18,-39,-10,-22,-20,-13,-9,15,-43,23,-9,14,20,-43,-31,-6,17,-14,-7,-31,-21,9,-29,4,14,-50,18,-10,-13,-3,59,16,0,15,-55,51,-9,8,-50,3,-8,12,-2,-21,-4,-16,-58,76,-45,61,-77,40,-56,4,1,-74,-1,1,-44,5,-24,67,-125,32,34,9,-45,-7,14,-10,-34,-40,16,24,-45,26,-11,10,17,-30,13,-81,50,-44,407,-916,1354,-182,2889,-2497,1006,1747,-3623,4711,-5360,1585,-773,336,-712,-1123,-441,1196,44,495,-30,-1130,-575,-219,-1190,-59,254,1226,98,470,447,289,463,-341,252,223,-335,11,-124,-661,-263,-216,-221,138,-355,262,-96,-329,-438,337,121,-414,6,-74,357,268,-17,508,-522,240,178,-237,-122,363,-84,-5,150,-216,-284,83,45,41,-483,948,-1284,2011,-3625,3548,-2481,254,144,139,111,-340,141,-105,38,99,-31,-77,46,-70,69,-11,-5,58,-51,-14,42,-49,32,-15,-13,-31,-27,-29,-36,-4,35,-21,-11,41,-38,-16,6,-2,14,-32,-19,-7,-16,16,23,-20,-31,-42,-10,4,6,36,-49,-16,-73,13,1,14,-81,-24,-14,-62,11,5,-16,-63,10,-14,19,-5,-9,14,-25,-49,45,-44,2,-6,-46,2,-13,4,-47,-3,15,-48,28,-66,61,-28,20,-44,8,-2,-41,-42,-2,-3,22,-31,-3,-29,8,-13,15,32,-80,16,-55,35,11,-26,-19,-10,9,-16,-32,11,14,-71,16,-16,79,-83,54,-421,803,-1688,2755,-1773,624,1393,-2193,4437,-5115,2206,-1209,-1833,1768,-716,-36,469,-199,541,786,-1112,-1147,-920,-1187,698,1246,117,338,-420,463,-1269,878,100,-301,256,-193,160,-1,922,359,180,380,182,-731,-1009,-226,-115,191,326,-201,554,272,-242,339,132,-40,-434,-16,-420,121,-343,-65,245,370,-252,296,20,-292,298,35,-49,-38,500,-416,327,-3311,4595,-3599,1183,280,-175,60,-706,690,-298,-129,-12,-9,139,-8,-153,-13,-51,69,30,-11,-17,10,-38,-8,17,10,9,22,-41,-12,-57,7,-11,7,-59,-8,20,-5,-19,11,-69,3,-32,17,-10,-23,-20,-34,12,-8,21,-51,4,-11,-44,28,-88,-5,-85,10,38,-49,-2,6,22,-18,-42,18,-21,-13,-67,5,-13,24,-53,-24,-23,-10,35,-89,2,6,-13,-30,-8,-14,7,-31,-34,1,-29,15,45,-18,-25,-8,-10,-5,36,-29,-38,13,-27,-9,-12,-16,25,-7,-16,-27,16,25,-21,-29,9,27,-18,-1,-9,-27,17,30,-19,49,-85,97,-2,108,-458,1380,-2711,1399,1208,-1490,5862,-6493,2444,-2336,-1153,1327,417,-1041,125,-2292,-425,672,135,1798,50,631,563,263,-776,828,-839,924,52,494,-414,301,-9,-51,-766,1501,51,2053,-208,-121,282,-359,-335,363,620,620,-188,-537,83,152,-153,-183,121,-103,-374,431,69,204,-2,928,-28,212,237,-409,507,-27,271,-72,132,-180,126,302,298,-2477,3232,-3316,1042,786,-1521,1245,-613,240,-456,225,47,-195,96,38,-57,123,-81,9,-23,-63,22,8,33,-24,-31,-71,12,-85,-23,21,-19,57,-70,-2,9,24,-22,10,-47,9,-3,-38,-14,-15,-28,-12,-9,5,-35,35,-15,-2,7,-43,-30,-7,26,-15,-17,-66,-20,-2,-39,4,-6,-64,29,-28,-15,-12,69,30,2,0,-71,55,-13,5,-27,-5,3,13,-9,-13,1,-4,-78,80,-78,88,-79,35,-61,-1,-4,-104,1,24,-62,10,-24,80,-129,19,67,-18,-66,-14,-0,-10,-34,-57,20,35,-54,16,-2,13,47,-34,-9,-83,62,-26,318,-945,1999,-619,1557,-170,1023,2843,-5081,5099,-2439,1446,629,187,17,-568,-46,1675,-782,34,204,792,318,285,-415,1078,-310,916,13,426,594,60,-234,834,123,413,97,-91,-396,-180,-13,-652,164,-173,-280,223,-281,-162,-70,624,-323,71,208,-325,-141,-16,-201,-500,-678,306,-28,-253,141,245,131,-15,46,-184,18,-70,-171,205,-323,1516,-1562,2979,-5016,3530,-2272,85,380,-2,230,-398,149,-71,19,103,-21,-123,25,-80,54,-13,-3,52,-42,-36,27,-47,27,-17,-0,-39,-26,-33,-46,-3,34,-23,-15,38,-39,-1,12,-13,31,-31,-37,-18,-13,16,23,-29,-10,-55,-43,20,4,36,-65,-16,-48,20,2,-8,-87,-20,-9,-64,21,-8,-24,-76,18,-21,12,-4,3,19,-19,-26,61,-51,2,5,-58,-6,-15,6,-49,-1,3,-71,44,-64,70,-23,36,-47,24,-0,-49,-37,1,-8,34,-30,-19,-36,-3,-15,15,50,-93,9,-76,32,15,-36,-6,-4,-1,-35,-40,25,1,-59,31,-49,86,-61,24,-359,904,-1616,3059,215,549,1964,-3594,5333,-5789,3712,-582,-1077,2140,-946,623,-775,955,1468,17,-483,83,-573,-397,558,-16,264,-381,-1125,497,-1552,-423,-1,284,286,-86,926,197,204,132,-410,70,-404,-202,-417,54,-441,361,248,-178,322,148,-8,208,-63,-189,-410,6,-139,87,-234,-521,24,-10,-417,436,-461,54,233,-189,32,-118,602,-895,71,-3943,4445,-3445,972,674,-328,84,-910,827,-248,-188,-5,-5,155,14,-154,-15,-75,59,42,-9,-13,-7,-42,-11,17,13,7,25,-51,-11,-51,-8,-2,19,-63,-23,21,2,0,11,-82,7,-19,-13,-6,-32,-12,-27,14,-8,27,-54,-4,-1,-56,42,-94,-14,-49,-2,54,-85,12,-5,13,4,-61,16,-19,2,-63,13,-4,27,-58,-27,-40,-13,47,-98,-4,3,-21,-9,5,-18,3,-38,-40,-2,-45,12,46,-34,-35,-10,-23,-1,31,-38,-30,18,-27,-19,-17,-9,16,-9,-25,-32,20,35,-21,-53,13,33,-26,-7,-11,-32,10,40,-47,65,-118,94,6,2,-398,1145,-1859,1388,1049,-3023,6049,-5879,1436,-2444,-1727,-294,600,-1728,-737,-1433,661,565,-867,1092,-1041,1753,-497,269,-1816,976,-620,638,-55,-141,-47,-53,282,319,-1299,917,-394,1899,-826,268,351,229,5,845,405,-72,-181,-377,-216,310,-463,-560,-286,249,-178,-342,-49,23,98,373,-109,111,-9,-195,351,175,445,-51,318,-222,322,-140,-67,-1862,2155,-1104,578,1608,-2133,1317,-620,292,-354,168,71,-224,95,57,-74,186,-73,9,-13,-80,23,12,30,-22,-13,-73,15,-90,-42,19,-20,52,-54,-2,-2,19,-17,7,-49,6,11,-36,-19,-8,-19,-8,-9,-11,-29,43,-22,-1,-30,-44,-27,-21,24,-21,-25,-81,-20,-13,-33,5,-7,-51,29,-30,-24,-31,55,25,2,-11,-79,44,-9,-4,7,-17,23,-6,-27,-2,0,17,-93,56,-115,104,-65,23,-56,-4,5,-101,9,45,-61,14,-17,70,-128,4,62,-54,-60,-16,-8,-3,-26,-48,27,36,-61,-5,6,10,63,-41,-18,-75,57,-2,188,-871,2309,-1201,78,1371,600,4019,-6086,4005,97,1453,1514,-384,429,882,528,826,-1181,-451,281,1505,684,50,-167,1202,-851,-257,-314,-125,346,-336,-662,915,-65,239,23,-137,-555,343,234,-398,114,-234,102,-6,-378,196,363,138,-482,372,290,-278,-310,-398,-471,-765,-337,-17,-65,-16,187,-5,171,-64,61,-74,380,-190,-235,253,-273,1655,-1725,3779,-5850,3068,-1664,-136,582,-179,333,-416,143,-61,18,108,-28,-147,29,-82,37,-10,-14,11,-23,-64,20,-34,23,-13,14,-27,-25,-24,-29,-1,12,-34,-27,9,-42,12,11,-24,36,-35,-40,-27,-22,9,16,-34,11,-55,-68,31,-5,48,-70,-11,-16,26,4,-23,-64,-20,-13,-50,31,-10,-30,-64,18,-9,-2,-1,7,15,-8,-18,65,-43,5,-8,-60,-21,-15,16,-34,1,-12,-68,45,-53,59,-16,33,-42,11,3,-42,-13,-5,-17,33,-23,-18,-29,-14,-11,9,42,-78,2,-69,13,5,-42,6,3,-6,-22,-34,23,-6,-29,20,-56,45,-29,-17,-129,626,-916,1945,1123,148,2054,-3293,3618,-3727,2876,136,29,739,-244,397,-1204,857,739,-754,101,1044,-92,273,-75,-615,10,-612,-256,-168,-70,-844,-117,186,-25,139,297,84,-305,-190,-381,-96,-206,91,200,127,-199,233,6,-32,9,-164,-27,-69,-103,-44,-3,-17,186,-65,23,-109,-39,-134,-61,20,-161,-15,-35,-99,10,-63,161,-410,-20,-996,894,-695,113,224,-125,42,-262,211,-47,-48,-38,-5,33,7,-39,-9,-22,-1,6,-8,-8,-9,-13,-11,-2,-1,-5,-2,-12,-6,-8,-2,0,0,-9,-7,0,0,1,-4,-10,0,-1,-6,-2,-4,0,-5,0,-2,2,-5,-3,1,-4,2,-4,-1,-2,-4,1,-6,1,-2,-0,1,-4,-1));

    //Normal Heart Sound for Testing
//    ArrayList<Integer> dataTest = new ArrayList<>(Arrays.asList(241,-6,-1602,79,2599,3455,287,-3172,-3540,-2137,-1273,905,4067,2697,398,-161,-266,-738,-1017,-1225,-318,-22,434,381,26,-467,-265,259,190,-224,-168,13,290,320,102,363,436,186,-85,-312,-160,-69,74,-286,-235,-136,103,85,267,101,-351,-371,-406,100,-21,200,370,427,198,-14,-63,-36,-319,-727,-660,-562,1102,2596,-428,-2589,-312,1812,256,-460,-403,-453,-567,-343,279,307,-12,-93,167,339,8,-252,-125,21,178,358,77,-143,-158,95,37,-165,-167,133,151,114,106,125,-64,-387,-393,75,387,123,-276,-363,-88,144,107,284,287,234,-2,-105,-338,-124,0,-187,-110,-65,22,148,128,-37,-113,115,203,58,88,205,153,-5,-55,-226,-213,-222,31,-19,142,-7,18,50,6,26,-29,-5,41,-30,71,35,-151,-225,-314,-206,148,316,331,263,32,-201,-269,-181,117,213,292,203,-133,-209,-287,-297,-26,240,214,137,3,-316,-225,-74,156,320,221,190,-83,-117,-8,-157,-118,-47,-45,-54,86,114,-1,42,-59,-67,-7,-23,265,156,5,-198,-266,-198,-163,-123,216,522,424,69,-382,-234,242,182,-397,-523,116,295,-199,-156,125,130,322,113,69,-35,99,-75,-105,-166,-550,179,77,-347,-1213,-120,3128,3422,315,-2836,-2708,-2516,-1875,197,3133,3487,914,-154,-406,-719,-1198,-1001,-743,-103,404,1011,693,130,-379,-210,-67,-318,-199,-163,41,421,291,386,268,37,-185,-74,-513,-462,30,147,478,193,-204,-8,56,-289,-383,-95,140,277,269,74,-34,-32,5,140,-3,40,-174,-251,-509,-417,-2116,4473,707,-1775,-1917,650,1868,745,-253,-551,-472,-292,-340,-169,128,240,198,200,114,-164,-316,-140,38,-119,-53,121,48,147,3,59,77,83,-11,-20,-211,-106,285,243,-166,-160,67,79,-68,-255,-11,82,-22,278,70,4,-31,7,59,-202,-239,-151,-32,72,205,108,57,42,-49,-72,-270,-283,95,242,234,240,148,38,-49,-60,-291,-176,-45,182,-144,161,99,-113,-433,-0,144,55,-111,78,58,110,-49,-1,85,32,-23,31,82,-57,154,106,-28,-48,-166,-13,-9,-63,-64,-60,2,53,91,106,49,10,-12,-23,-148,-89,43,134,-90,-167,-10,122,1,-61,108,161,24,-13,-53,-2,119,10,-36,-195,-186,-121,-70,173,60,-154,111,260,394,227,-19,-465,-223,-29,-176,-456,-190,274,365,143,179,222,184,137,11,-128,-112,-262,-514,-541,-175,410,431,-292,-757,1234,2615,1374,-1473,-2804,-1167,-1405,-1456,1084,2716,3114,751,-394,-888,-1448,-1396,-685,-84,250,704,830,581,-25,-456,-112,-107,-63,-96,41,-126,-29,-116,189,294,262,98,-86,64,71,-451,-430,-13,379,67,-102,-232,-246,186,130,-38,81,125,12,40,-80,38,52,326,146,-137,-392,-594,-408,-2830,6156,491,-1091,-2442,92,1665,1242,240,-696,-846,-269,-202,2,131,148,188,157,19,-201,-194,-255,-121,2,163,147,132,-16,-175,-123,107,-75,-166,29,209,95,133,107,298,246,11,-195,-229,-216,-177,-227,-183,-23,117,173,366,214,175,-57,-189,2,-8,-165,-166,-215,-38,150,99,107,76,74,-2,-26,-24,-62,-145,-215,-4,117,36,112,179,34,36,-90,-97,-32,-115,-55,102,110,4,-122,-209,151,161,16,52,-18,-176,-208,53,148,38,96,221,16,-170,-102,-87,-59,25,104,42,-210,-139,-67,87,216,4,-151,-82,73,4,168,42,-15,94,-47,-39,13,-121,-101,-160,34,76,78,85,9,-163,-264,-89,382,361,328,122,-441,-419,-379,20,381,285,-341,-304,-381,301,431,222,249,133,104,58,-181,-209,-579,-596,-163,215,-193,-183,789,2323,2444,-1238,-3885,-1584,288,-85,245,2184,1348,710,-994,-665,-532,-741,-720,-37,199,456,603,622,92,-159,-163,-82,-153,-499,-268,-116,476,239,-47,90,172,74,324,-62,-240,-212,79,-279,-119,-150,232,83,-220,-119,161,63,40,118,1,-22,266,-16,-128,-157,-84,68,167,-104,-303,-2258,1137,4081,-3048,-3514,1920,1029,1038,275,92,-612,-1220,-632,132,430,386,101,8,194,16,-265,-457,-269,-161,104,98,109,261,132,187,-21,27,118,29,-220,-243,67,244,181,-52,87,-79,3,-100,-53,-49,-13,-85,-51,51,-38,40,122,144,36,-152,-37,52,-47,-8,56,250,108,-131,-49,-255,-189,-133,-24,70,222,240,249,87,-150,-232,-72,30,2,-74,-78,-124,139,124,75,262,157,-55,-190,-237,-31,-43,99,172,-18,144,68,-57,-55,-30,-103,-210,-23,181,143,144,-47,-182,-32,98,72,-73,-375,-278,10,483,391,345,-44,-215,-394,-156,237,660,92,-669,-887,-435,210,513,210,218,275,342,355,60,-432,-826,-775,-9,868,141,-929,-181,2259,2617,-634,-982,-4489,-39,761,1038,653,604,345,-468,-74,-266,-359,-214,85,66,177,193,255,204,-115,-152,-194,-172,141,-288,-247,-143,531,551,367,104,42,-88,-356,-383,-378,-110,259,162,235,-99,-43,21,-73,-67,-42,69,16,-84,15,199,202,136,27,6,-111,-186,-407,-489,-1942,6118,1300,-3078,-3311,279,2246,1310,1948,-308,-905,-70,-372,-713,-663,-195,303,374,62,14,-66,-292,-255,-93,18,135,121,237,321,265,175,181,-72,-298,-246,-219,16,94,145,-88,-52,-83,42,27,98,-84,-301,-83,-30,-105,210,633,329,123,20,-127,-343,-406,-270,-100,79,97,86,176,132,29,114,-57,-122,-271,-146,97,178,124,161,91,-2,-16,-150,-300,-176,-92,-138,197,403,284,86,15,-107,-81,-36,-175,226,190,-120,-460,-385,-196,121,421,416,185,57,25,-102,-207,-285,-306,-368,67,603,871,-531,-1157,-398,2119,2743,1251,-3093,-4010,-2147,1179,1985,1888,772,-186,352,-383,-453,-666,-645,-276,318,203,107,434,30,-81,-13,23,-49,-248,4,149,-41,115,421,475,129,-479,-488,-421,-181,103,20,64,112,103,138,243,49,-75,-46,-150,-97,157,74,91,-114,-290,92,201,116,40,-201,-533,-1797,2923,2369,-2379,-4273,-488,2436,1892,201,603,-301,-1109,-8,24,-217,-782,-479,247,498,361,-130,-487,-381,88,309,348,193,126,33,32,14,190,194,-201,-395,-210,-22,16,8,-85,18,290,136,-78,-231,-325,-9,160,163,175,172,226,48,-212,-294,-185,-319,-10,186,348,178,-47,56,0,-146,-149,-24,-59,35,158,72,198,285,-137,-341,-287,262,522,370,-333,-840,-587,-635,-302,508,861,839,470,35,-278,-174,-43,-206,-291,-479,-365,258,710,-259,-1528,-474,3204,4019,1359,-4947,-7032,-1448,2253,4225,3328,536,-599,-1536,-915,-291,-193,-253,-482,155,347,101,152,146,-121,-171,-23,13,-52,112,298,44,-108,129,286,337,17,-235,-675,-432,28,230,139,16,148,-42,-132,-158,181,335,311,-31,-370,-229,1,198,-36,-97,-75,63,190,226,-9,-594,-1158,-1699,3972,406,-4008,-1488,697,1956,1696,481,62,-412,-1098,-1073,-187,366,175,209,14,84,-112,-269,-184,-15,23,191,261,154,-128,-83,-72,161,140,42,26,-192,-307,38,222,419,3,-237,-296,-239,-240,-36,278,280,251,146,-2,-13,-179,-211,-154,-29,194,160,67,-140,-208,-68,-99,111,220,174,128,-94,-135,-246,-103,-85,107,205,171,-18,63,-5,16,-3,-168,-95,-94,-70,36,54,61,48,-82,-18,188,-116,49,137,105,-217,-11,198,90,-133,-430,-189,470,471,-154,-678,-457,71,357,245,71,204,163,56,-6,-66,33,-169,-488,-528,-338,18,128,440,352,335,1499,1848,134,-1848,-2676,-3180,-1229,1674,3331,2443,384,-164,-1165,-821,-311,-478,-253,-154,29,457,475,16,-70,-168,-131,-73,-112,-353,-40,79,285,349,-30,19,292,272,-151,-136,-84,-339,-156,-39,84,-26,81,-182,17,99,213,30,27,-7,-166,70,66,44,-107,-87,133,19,-490,86,-257,-6,-651,-3582,7319,1128,-4244,-1237,1478,626,60,-36,-1,-175,-215,-135,232,-143,-162,-80,20,-34,97,98,62,-195,-110,47,176,142,157,-36,-107,8,-64,-284,-164,120,61,89,248,152,284,-8,-39,-255,-181,-358,-398,66,275,251,202,238,141,-91,-40,-64,-77,-136,-115,68,-78,-187,-174,-118,107,164,196,107,25,146,166,76,-140,-207,-104,-171,-26,48,-5,-38,129,197,-13,-19,-34,80,89,-54,60,13,18,-17,-193,-164,-155,63,431,-7,-208,-143,187,243,95,-140,-110,68,58,45,-43,-114,-139,39,12,15,-60,-42,-109,72,126,182,45,53,9,22,66,-28,-247,-41,18,-58,65,78,-58,-28,118,98,-133,-144,10,25,121,-40,52,-105,-80,130,201,141,-153,-33,43,-278,-320,-16,172,267,28,61,-57,15,3,49,-50,-146,-182,-92,68,53,-125,-79,228,297,188,214,28,-313,-244,-31,-327,-761,-146,470,597,451,367,87,-190,-193,-165,-67,-16,120,-39,-461,-561,-145,272,-39,-419,-722,1586,3419,1434,-1432,-3501,-1425,-1476,-1120,1382,2469,2554,331,-369,-407,-1054,-1129,-772,-161,356,702,758,293,-184,-534,-277,260,-301,92,-374,118,221,169,383,175,-147,-49,42,-228,-158,-250,101,-193,-120,-168,142,186,326,164,-103,-234,-21,361,27,-123,7,-5,-4,-20,50,31,-176,-156,-555,-23,-434,-3406,7659,1770,-4307,-2011,1865,1280,-2,-372,-95,58,-175,-194,-7,-55,-93,4,-80,55,82,75,-78,-135,-177,96,163,369,221,-204,-290,-203,40,137,-107,-144,69,141,88,95,171,-5,56,-64,11,24,-63,-260,-253,-66,52,49,207,418,161,-165,-68,-105,-47,94,53,-112,-90,-20,-157,21,80,87,37,-48,-62,45,74,115,114,-12,-131,-91,-120,-20,1,-44,151,71,135,38,-467,341,-408,-526,258,374,112,123,104,-117,-290,-142,152,252,-58,-61,2,31,-49,-4,168,-53,-78,157,-75,-158,-142,-119,22,99,141,-38,13,212,175,40,-27,-126,-239,-278,-108,162,161,193,135,-121,-54,-46,26,-50,-25,-63,-34,57,109,21,-86,29,147,-110,-168,55,108,-279,-157,260,129,126,367,196,-353,-399,-35,58,-424,-470,15,320,480,227,136,103,194,270,100,-349,-498,-320,-282,-302,-262,-63,359,654,325,582,1687,1379,-2789,-2878,-1156,295,742,1595,1408,124,163,-591,-852,-135,-63,-638,-110,314,573,373,283,25,148,-292,-546,-222,-319,-103,217,402,265,180,118,-67,-73,159,-90,12,-258,-47,-150,105,-39,-167,-20,91,78,146,-116,-113,-210,129,388,232,20,-248,-125,-97,92,80,-109,-167,-202,-554,-567,-1151,4098,257,-3434,-2195,1092,2210,673,-453,-1111,-222,39,204,25,-38,100,75,-12,-37,-66,-20,-136,-298,-135,185,47,177,83,96,-26,14,81,67,-7,-281,-309,11,400,338,229,-161,-141,-182,-152,-70,53,78,-110,-212,-8,-43,152,375,292,90,-44,-122,-243,-116,-344,-182,41,128,28,60,72,124,174,-143,-7,-54,-22,-153,90,180,77,-65,-29,42,-73,-282,-99,80,159,118,252,-45,-145,24,-0,-141,46,-24,51,-71,-15,158,41,10,8,22,89,8,-139,-75,2,-117,-102,53,32,100,265,133,-137,-89,-113,-55,19,169,110,-34,-149,-126,-20,-35,44,113,177,-25,109,24,-265,-214,448,332,-188,-378,-655,-343,-69,433));


    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private final String TAG = MainFragment.class.getSimpleName();
    private Handler mHandler; // Our main handler that will receive callback notifications
    private ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier

    // defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status


    public MainFragment() {}


    public static MainFragment newInstance() {
        return new MainFragment();
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Set up and subscribe (observe) to the ViewModel
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((BluetoothSensor) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        DataEntityDatabase db = Room.databaseBuilder(getActivity().getApplicationContext(),
                DataEntityDatabase.class, "heart-data").build();

        mHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == MESSAGE_READ) {
                    String readMessage = "hi";
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    // mReadBuffer.setText(readMessage);

                    for (String value : readMessage.split("/")) {
                        // mReadBuffer.setText(value);
                        double y = 0.0;
                        try {
                            y = Double.valueOf(value) * (5.0 / 1023.0); //converts int to voltage double
                            mReadBuffer.setText(String.valueOf(y));
                            if (y != 0) zero = true;
                        } catch (NumberFormatException e) {
                            y = 0.0; // your default value
                        }
                        if (zero) addDataPoint(y);
                    }
                }


                if (msg.what == CONNECTING_STATUS) {
                    if (msg.arg1 == 1)
                        mBluetoothStatus.setText("Connected to Device: " + (msg.obj));
                    else
                        mBluetoothStatus.setText("Connection Failed");
                }
            }
        };


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_main, container, false);
        mBluetoothStatus = v.findViewById(R.id.bluetoothStatus);
        mReadBuffer = v.findViewById(R.id.readBuffer);
        mOnBtn = v.findViewById(R.id.on);
        mOffBtn = v.findViewById(R.id.off);



        mDiscoverBtn = v.findViewById(R.id.discover);
        mListPairedDevicesBtn = v.findViewById(R.id.PairedBtn);
        mViewGraphBt = v.findViewById(R.id.viewGraph);

        mSaveData = v.findViewById(R.id.save_data);
        mPrevData = v.findViewById(R.id.all_data);

        mBTArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio

        if(mBTAdapter.isEnabled()) {
            mOnBtn.setVisibility(View.GONE);
            mOffBtn.setVisibility(View.VISIBLE);
            mBluetoothStatus.setText("Enabled");
        }
        else {
            mOffBtn.setVisibility(View.GONE);
            mOnBtn.setVisibility(View.VISIBLE);
            mBluetoothStatus.setText("Disabled");
        }

        mDevicesListView = v.findViewById(R.id.devicesListView);
        mDevicesListView.setAdapter(mBTArrayAdapter); // assign model to view
        mDevicesListView.setOnItemClickListener(mDeviceClickListener);


//         Ask for location permission if not already allowed
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);


        if (mBTArrayAdapter == null) {
            // Device does not support Bluetooth
            mBluetoothStatus.setText("Status: Bluetooth not found");
            Toast.makeText(getActivity().getApplicationContext(), "Bluetooth Device Not Found!", Toast.LENGTH_SHORT).show();
        }
        else {
            mSaveData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data.size() != 0) {
                        mConnectedThread.cancel(); //ADD THIS BACK!!!!!!

                        Intent intent = new Intent(getActivity(), SaveDataActivity.class);

                        //For Testing Heart Sound Data
//                        double[] dataArray = new double[dataTest.size()];
//
//                        for (int i = 0; i < dataTest.size(); i++) {
//                            dataArray[i] = Double.valueOf(dataTest.get(i))/1000;
//                        }

                        double[] dataArray = new double[data.size()];

                        // ArrayList to Array Conversion
                        for (int i = 0; i < data.size(); i++) {
                            dataArray[i] = data.get(i);
                        }

                        intent.putExtra("length", data.size());
                        intent.putExtra("data", dataArray);
                        startActivity(intent);
                    } else
                        Toast.makeText(getActivity().getApplicationContext(), "No Existing Data To Save", Toast.LENGTH_SHORT).show();
                }

            });

            mPrevData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DataEntityCollectionViewModel entityCollectionViewModel = ViewModelProviders.of(getActivity(), viewModelFactory)
                            .get(DataEntityCollectionViewModel.class);
                    if (entityCollectionViewModel.getDataEntities() != null) {
                        Intent intent = new Intent(getActivity(), AllDataEntitiesActivity.class);
                        startActivity(intent);
                    } else
                        Toast.makeText(getActivity().getApplicationContext(), "No Previous Data Stored", Toast.LENGTH_SHORT).show();
                }

            });


            mOnBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bluetoothOn(v);
                }
            });

            mOffBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bluetoothOff(v);
                }
            });

            mListPairedDevicesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listPairedDevices(v);
                }
            });

            mDiscoverBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    discover(v);
                }
            });

            mViewGraphBt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (data.size() != 0) {
                        Intent intent = new Intent(getActivity(), GraphActivity.class);

                        //For Data Collecting
                        double[] dataArray = new double[data.size()];

                        // ArrayList to Array Conversion
                        for (int i = 0; i < data.size(); i++) {
                            dataArray[i] = data.get(i);
                        }
                        intent.putExtra("length", data.size());

                        //For Testing Heart Sound Data
//                        double[] dataArray = new double[dataTest.size()];

//                        for (int i = 0; i < dataTest.size(); i++) {
//                            dataArray[i] = Double.valueOf(dataTest.get(i))/1000;
//                        }
//
//                        intent.putExtra("length", dataTest.size());
//                        intent.putExtra("data", dataArray);

                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "No Data Available to Graph", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        return v;
    }

    private void bluetoothOn(View view) {
        mOnBtn.setVisibility(View.GONE);
        mOffBtn.setVisibility(View.VISIBLE);
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            mBluetoothStatus.setText("Enabled");
            Toast.makeText(getActivity().getApplicationContext(), "Bluetooth Turned On", Toast.LENGTH_SHORT).show();
    }

    // Enter here after user selects "yes" or "no" to enabling radio
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent Data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                mBluetoothStatus.setText("Enabled");
            } else
                mBluetoothStatus.setText("Disabled");
        }
    }

    private void bluetoothOff(View view) {
        mOffBtn.setVisibility(View.GONE);
        mOnBtn.setVisibility(View.VISIBLE);
        mBTAdapter.disable(); // turn off
        mBluetoothStatus.setText("Disabled");
        Toast.makeText(getActivity().getApplicationContext(), "Bluetooth Turned Off", Toast.LENGTH_SHORT).show();
        mBTArrayAdapter.clear();
    }

    private void discover(View view) {
        // Check if the device is already discovering
        if (mBTAdapter.isDiscovering()) {
            mBTAdapter.cancelDiscovery();
            Toast.makeText(getContext(), "Discovery Stopped", Toast.LENGTH_SHORT).show();
        } else {
            if (mBTAdapter.isEnabled()) {
                mBTArrayAdapter.clear(); // clear items
                mBTAdapter.startDiscovery();
                Toast.makeText(getContext(), "Discovery Started", Toast.LENGTH_SHORT).show();
                //problem is here (below)
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                getActivity().registerReceiver(blReceiver, filter);
            } else {
                Toast.makeText(getContext(), "Bluetooth Not On", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name to the list
                String name = device.getName();
                if(name == null)
                    name = "Unnamed Device";
                mBTArrayAdapter.add(name + "\n" + device.getAddress());
                mBTArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    private void listPairedDevices(View view) {
        mPairedDevices = mBTAdapter.getBondedDevices();
        if (mBTAdapter.isDiscovering()) {
            mBTAdapter.cancelDiscovery();
        }
        mBTArrayAdapter.clear();
        if (mBTAdapter.isEnabled()) {
            // put it's one to the adapter
            for (BluetoothDevice device : mPairedDevices)
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());

            Toast.makeText(getActivity().getApplicationContext(), "Show Paired Devices", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(getActivity().getApplicationContext(), "Bluetooth Not On", Toast.LENGTH_SHORT).show();
    }

    private AdapterView.OnItemClickListener mDeviceClickListener;

    {
        mDeviceClickListener = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

                if (!mBTAdapter.isEnabled()) {
                    Toast.makeText(getActivity().getBaseContext(), "Bluetooth Not On", Toast.LENGTH_SHORT).show();
                    return;
                }


                mBluetoothStatus.setText("Connecting...");
                // Get the device MAC address, which is the last 17 chars in the View
                String info = ((TextView) v).getText().toString();
                final String address = info.substring(info.length() - 17);
                final String name = info.substring(0, info.length() - 17);

                // Spawn a new thread to avoid blocking the GUI one
                new Thread() {
                    public void run() {
                        boolean fail = false;

                        BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

                        try {
                            mBTSocket = createBluetoothSocket(device);
                        } catch (IOException e) {
                            fail = true;
                            Toast.makeText(getActivity().getBaseContext(), "Socket Creation Failed", Toast.LENGTH_SHORT).show();
                        }
                        // Establish the Bluetooth socket connection.
                        try {
                            mBTSocket.connect();
                        } catch (IOException e) {
                            try {
                                fail = true;
                                mBTSocket.close();
                                mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                        .sendToTarget();
                            } catch (IOException e2) {
                                //insert code to deal with this
                                Toast.makeText(getActivity().getBaseContext(), "Socket Creation Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (fail == false) {
                            mConnectedThread = new ConnectedThread(mBTSocket);
                            mConnectedThread.start();

                            mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                                    .sendToTarget();
                        }
                    }
                }.start();
            }
        };
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BTMODULEUUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection", e);
        }
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;

        }

        public void run() {
            byte[] buffer;  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.available();
                    if (bytes >= 0) {
                        buffer = new byte[1024];
                        Thread.sleep(1000, 500000);
                        bytes = mmInStream.available(); // how many bytes are ready to be read?
                        bytes = mmInStream.read(buffer, 0, bytes); // record how many bytes we actually read
                        mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                                .sendToTarget(); // Send the obtained bytes to the UI activity
                    }

                } catch (IOException e) {
                    e.printStackTrace();

                    break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }


        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }


    private void addDataPoint(double y) {
        data.add(y);
    }

}
