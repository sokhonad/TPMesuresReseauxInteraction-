# TPMesuresReseauxInteraction
Question 2 : 
------------

Le graphe des collaborations scientifiques possède:
- 317 080 Noeuds
- 1 049 866 Liens
- un degré moyen de 6.62
- un coefficient de clustering de 0.632

Pour un graph aléatoire de la même taille et du même degré moyen le coefficient de clustering est de 3.7022436744750736E-5

Question 3 :
------------
Le graph est connexe, un graph aléatoire de  la même taille et du même degré moyen n'est  pas connexe.

Question 4 :
------------
La distribution des degrés dans le graphe suis la fonction suivante : 

![distributivite](distributionlIneaire.png)
![distributivite](DistributionLogLog.png)

Question 5:
------------
La distance moyenne pour Le graphe des collaborations scientifiquesest de 8.294

Pour le graph aléatoire de graphStream La distance moyenne est de 7.697

# Propagation
#Question 1:

##taux de propagation du virus : 

$\lambda = \frac{\beta}{\mu}$
$\lambda = \frac{\frac{1}{7}}{\frac{1}{14}}$

##le seuil épidémique : 

$\lambda_c = \frac{<k>}{<k^{2}>}$

###Pour le graph du réseau scientifique

$\lambda_c = \frac{6.62}{144.01}$
$\lambda_c = 0.0459$

###Pour un graph aléatoire :

$\lambda_c = \frac{1}{<k> + 1}$
$\lambda_c = \frac{1}{7.62}$
$\lambda_c = 0.13$

