package util;

public class WGS_Encrypt {
	private static double pi = 3.14159265358979324;
    private static double a = 6378245.0;
    private static double ee = 0.00669342162296594323;
    private static double x_pi = 3.14159265358979324 * 3000.0 / 180.0;
    
    public static  Boolean outofChina(double lat, double lon)
    {
        if (lon < 72.004 || lon > 137.8374)
            return true;
        if (lat < 0.8293 || lat > 55.8271)
            return true;
        return false;
    }

    private static double transformLat(double x, double y)
    {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double transformLon(double x, double y)
    {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0 * pi)) * 2.0 / 3.0;
        return ret;  
    }

    /// <summary>
    /// 地球坐标转换为火星坐标（GCJ02）
    /// </summary>
    /// <param name="wglat">地球纬度坐标</param>30 y
    /// <param name="wglon">地球经度坐标</param>114 x
    /// <param name="mglat">火星纬度坐标</param>
    /// <param name="mglon">火星经度坐标</param>
    public static double[]  WGS2Mars(double wgLat, double wgLon)
    {
    	double[] result= new double[2];
    	double mgLat=0;
    	double mgLon=0;
        if (outofChina(wgLat, wgLon))
        {
            mgLat = wgLat;
            mgLon = wgLon;
            return result;
        }
        double dLat = transformLat(wgLon - 105.0, wgLat - 35.0);
        double dLon = transformLon(wgLon - 105.0, wgLat - 35.0);
        double radLat = wgLat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        mgLat = wgLat + dLat;
        mgLon = wgLon + dLon;
       
        result[0]=mgLat;
        result[1]=mgLon;
        return result;
    }

    /// <summary>
    /// 火星坐标（GCJ02）转换为百度坐标（BD09）
    /// </summary>
    /// <param name="mg_Lat">火星纬度坐标</param>
    /// <param name="mg_lon">火星经度坐标</param>
    /// <param name="bd_Lat">百度纬度坐标</param>
    /// <param name="bd_Lon">百度经度坐标</param>
    public static double[] Mars2BD(double mg_Lat, double mg_lon)
    {
    	double[] result= new double[2];
    	double bd_Lat;
    	double bd_Lon;
        double x = mg_lon, y = mg_Lat;
        double z = Math.sqrt(x*x + y*y) + 0.00002*Math.sin(y*x_pi);
        double theta = Math.atan2(y, x) + 0.000003*Math.cos(x*x_pi);
        bd_Lon = z*Math.cos(theta) + 0.0065;
        bd_Lat = z*Math.sin(theta) + 0.006;
        
        result[0]=bd_Lat;
        result[1]=bd_Lon;
        return result;
    }

    /// <summary>
    /// 火星坐标转为WGS84坐标
    /// </summary>
    /// <param name="gcj_Lon">火星坐标经度</param>
    /// <param name="gcj_Lat">火星坐标纬度</param>
    /// <param name="wgs_Lon">WGS经度</param>
    /// <param name="wgs_Lat">WGS纬度</param>
    public static double[]  GCJ2WGS(double gcj_Lat, double gcj_Lon)
    {
    	double[] result= new double[2];
    	double wgs_Lon;
    	double wgs_Lat;
        double inter_Lon;
        double inter_Lat;
        double[] inters=WGS2Mars(gcj_Lat,gcj_Lon);
        inter_Lat=inters[0];
        inter_Lon=inters[1];
        wgs_Lon = gcj_Lon*2 - inter_Lon;
        wgs_Lat = gcj_Lat*2 - inter_Lat;
        result[0]=wgs_Lat;
        result[1]=wgs_Lon;
        return result;
    }

    /// <summary>
    /// 地球坐标转换为百度坐标（BD09）
    /// </summary>
    /// <param name="wg_lat">地球纬度坐标</param>
    /// <param name="wg_lon">地球经度坐标</param>
    /// <param name="bd_lat">百度纬度坐标</param>
    /// <param name="bd_lon">百度经度坐标</param>
    public static double[] WGS2BD(double wg_lat, double wg_lon)
    {
    	double[] result= new double[2];
        double[] mgs=WGS2Mars(wg_lat, wg_lon);
        double[] bds=Mars2BD(mgs[0], mgs[1]);
        result[0]=bds[0];
        result[1]=bds[1];
        return result;
    }

    /// <summary>
    /// 百度坐标转为火星坐标
    /// </summary>
    /// <param name="bd_lat"></param>
    /// <param name="bd_lon"></param>
    /// <param name="mg_lat"></param>
    /// <param name="mg_lon"></param>
    public static double[] BD2Mars(double bd_lat, double bd_lon)
    {
    	double[] result= new double[2];
        double x = bd_lon - 0.0065, y = bd_lat - 0.006;
        double z = Math.sqrt(x*x + y*y) - 0.00002*Math.sin(y*x_pi);
        double theta = Math.atan2(y, x) - 0.000003*Math.cos(x*x_pi);
        double mg_lon = z*Math.cos(theta);
        double mg_lat = z*Math.sin(theta);
        result[0]=mg_lat;
        result[1]=mg_lon;
        return result;
    }
}
