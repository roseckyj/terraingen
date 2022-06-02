package cz.xrosecky.terraingen.utils;

// lat = NS = x = phi
// lon = WE = z = lambda

public class Coords {
    private static final double EARTH_RADIUS = 6371 * 1000;
    private static final Pointf2D MAP_CENTER = new Pointf2D(rad(49.2100008), rad(16.5983761)); // FI MUNI
    // private static final Pointf2D MAP_CENTER = new Pointf2D(rad(49.195063), rad(16.608272)); // Namesti svobody
    private static final double ROTATION = rad(-22 + 90);

    public static int normalizeY(double alt) {
        // Check if this is good
        int y = (int)Math.round(alt);
        y -= 100;
        return y;
    }

    public static double denormalizeY(double y) {
        y += 100;
        return y;
    }


    // Using Orthographic projection https://en.wikipedia.org/wiki/Orthographic_map_projection
    public static Pointf2D latLonToXZ(double lat, double lon) {
        lat = rad(lat);
        lon = rad(lon);

        double deltaLon = lon - MAP_CENTER.lon();
        double x = EARTH_RADIUS * Math.cos(lat) * Math.sin(deltaLon);
        double z = EARTH_RADIUS * (Math.cos(MAP_CENTER.lat()) * Math.sin(lat) - Math.sin(MAP_CENTER.lat()) * Math.cos(lat) * Math.cos(deltaLon));

        double x2 = x * Math.cos(ROTATION) - z * Math.sin(ROTATION);
        double z2 = x * Math.sin(ROTATION) + z * Math.cos(ROTATION);

        x = x2;
        z = z2;

        x *= 1;
        z *= -1;

        return new Pointf2D(x, z);
    }

    public static Pointf2D latLonToXZ(Pointf2D point) {
        return latLonToXZ(point.x, point.z);
    }

    public static Pointf3D latLonToXZ(Pointf3D point) {
        return new Pointf3D(latLonToXZ(point.x, point.z), normalizeY(point.y));
    }

    public static Pointf2D XZToLatLon(double x, double z) {
        x *= 1;
        z *= -1;

        double x2 = x * Math.cos(-ROTATION) - z * Math.sin(-ROTATION);
        double z2 = x * Math.sin(-ROTATION) + z * Math.cos(-ROTATION);

        x = x2;
        z = z2;

        double rho = Math.sqrt(x*x + z*z);
        double c = Math.asin(rho / EARTH_RADIUS);
        double cosC = Math.cos(c);
        double sinC = Math.sin(c);
        double cosPhi0 = Math.cos(MAP_CENTER.lat());
        double sinPhi0 = Math.sin(MAP_CENTER.lat());

        double lat = rho == 0 ? MAP_CENTER.lat() : Math.asin(cosC * sinPhi0 + (z * sinC * cosPhi0) / rho);
        double lon = MAP_CENTER.lon() + Math.atan2(x * sinC, rho * cosC * cosPhi0 - z * sinC * sinPhi0);

        return new Pointf2D(deg(lat), deg(lon));
    }

    public static Pointf2D XZToLatLon(Pointf2D point) {
        return XZToLatLon(point.x, point.z);
    }

    public static Pointf3D XZToLatLon(Pointf3D point) {
        return new Pointf3D(XZToLatLon(point.x, point.z), denormalizeY(point.y));
    }

    /* Adapted from http://martin.hinner.info/geo/ */
    public static Pointf3D krovakToLatLonAlt(double Y, double X, double H) {
        if (X < 0 && Y < 0) {
            X = -X;
            Y = -Y;
        }
        H += 45;

        /* Vypocet zemepisnych souradnic z rovinnych souradnic */
        double e=0.081696831215303;
        double n=0.97992470462083;
        double konst_u_ro=12310230.12797036;
        double sinUQ=0.863499969506341;
        double cosUQ=0.504348889819882;
        double sinVQ=0.420215144586493;
        double cosVQ=0.907424504992097;
        double alfa=1.000597498371542;
        double k=1.003419163966575;
        double ro=Math.sqrt(X*X+Y*Y);
        double epsilon=2*Math.atan(Y/(ro+X));
        double D=epsilon/n;
        double S=2*Math.atan(Math.exp(1/n*Math.log(konst_u_ro/ro)))-Math.PI/2;
        double sinS=Math.sin(S);
        double cosS=Math.cos(S);
        double sinU=sinUQ*sinS-cosUQ*cosS*Math.cos(D);
        double cosU=Math.sqrt(1-sinU*sinU);
        double sinDV=Math.sin(D)*cosS/cosU;
        double cosDV=Math.sqrt(1-sinDV*sinDV);
        double sinV=sinVQ*cosDV-cosVQ*sinDV;
        double cosV=cosVQ*cosDV+sinVQ*sinDV;
        double Ljtsk=2*Math.atan(sinV/(1+cosV))/alfa;
        double t=Math.exp(2/alfa*Math.log((1+sinU)/cosU/k));
        double pom=(t-1)/(t+1);
        double sinB;
        do {
            sinB=pom;
            pom=t*Math.exp(e*Math.log((1+e*sinB)/(1-e*sinB)));
            pom=(pom-1)/(pom+1);
        } while (Math.abs(pom-sinB)>1e-15);

        double Bjtsk=Math.atan(pom/Math.sqrt(1-pom*pom));


        /* Pravoúhlé souřadnice ve S-JTSK */
        double a=6377397.15508;
        double f_1=299.152812853;
        double e2=1-(1-1/f_1)*(1-1/f_1);
        ro=a/Math.sqrt(1-e2*Math.sin(Bjtsk)*Math.sin(Bjtsk));
        double x=(ro+H)*Math.cos(Bjtsk)*Math.cos(Ljtsk);
        double y=(ro+H)*Math.cos(Bjtsk)*Math.sin(Ljtsk);
        double z=((1-e2)*ro+H)*Math.sin(Bjtsk);

        /* Pravoúhlé souřadnice v WGS-84*/
        double dx=570.69;
        double dy=85.69;
        double dz=462.84;
        double wz=-5.2611/3600*Math.PI/180;
        double wy=-1.58676/3600*Math.PI/180;
        double wx=-4.99821/3600*Math.PI/180;
        double m=3.543e-6;
        double xn=dx+(1+m)*(x+wz*y-wy*z);
        double yn=dy+(1+m)*(-wz*x+y+wx*z);
        double zn=dz+(1+m)*(wy*x-wx*y+z);

        /* Geodetické souřadnice v systému WGS-84*/
        a=6378137.0; f_1=298.257223563;
        double a_b=f_1/(f_1-1);
        double p=Math.sqrt(xn*xn+yn*yn);
        e2=1-(1-1/f_1)*(1-1/f_1);
        double theta=Math.atan(zn*a_b/p);
        double st=Math.sin(theta);
        double ct=Math.cos(theta);
        t=(zn+e2*a_b*a*st*st*st)/(p-e2*a*ct*ct*ct);
        double B=Math.atan(t);
        double L=2*Math.atan(yn/(p+xn));
        H=Math.sqrt(1+t*t)*(p-a/Math.sqrt(1+(1-e2)*t*t));

        /* Formát výstupních hodnot */
        return new Pointf3D(deg(B), deg(L), H);
    }

    private static double rad(double deg) {
        return deg / 180 * Math.PI;
    }

    private static double deg (double rad) {
        return rad / Math.PI * 180;
    }
}
