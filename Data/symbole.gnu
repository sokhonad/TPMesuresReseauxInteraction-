
set terminal png
set encoding utf8
set xrange [0:345]
set yrange [0:0.189]
set xlabel "x"
set output 'DistributiondedegrespLineaire.png'
plot "distribition.data" t "Distribution de degres Lineaire" with linesp lt 1 pt 1

set terminal png
set encoding utf8
set yrange [1e-6:1]
set logscale xy
set xlabel "x"
set output 'DistributiondedegresLoglog.png'
plot "distribition.data" t "Distribution de degrees LogLog" with linesp lt 1 pt 1


set terminal png
set title "Degree distribution"
set xlabel 'k'
set ylabel 'p(k)'
set output 'dd_dblp.png'

set logscale xy
set yrange [1e-6:1]

# Poisson
lambda = 6.62208890914917
poisson(k) = lambda ** k * exp(-lambda) / gamma(k + 1)

# on va fitter une fonction linéaire en log-log

f(x) = lc - gamma * x
fit f(x) 'distribition.data' using (log($1)):(log($2)) via lc, gamma

c = exp(lc)
power(k) = c * k ** (-gamma)

plot 'distribition.data' title 'DBLP', \
  poisson(x) title 'Poisson law', \
  power(x) title 'Power law'

#**********
set terminal x11
set encoding utf8
set xrange [0:22]
set yrange [0:0.3]
set xlabel "x"

plot "DistributionDistanceGrapheAleatoireGraphStream.data" t "Distribution de distance" with linesp lt 1 pt 1


set terminal x11
set encoding utf8
set xrange [1:22]
set yrange [0:0.3]
set xlabel "x"
set output 'distribition distance.png'
plot "DistributionDistanceGrapheAleatoireGraphStream.data" t "Distribution des distances" with linesp lt 1 pt 1

