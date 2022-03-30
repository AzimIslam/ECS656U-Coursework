import random, decimal
import sys

numOfDimensions = int(sys.argv[1])
rng = int(sys.argv[2])

for i in range(numOfDimensions):
    row = ""
    for j in range(numOfDimensions):
        randomDecimal = decimal.Decimal('%d.%d' % (random.randint(rng*-1,rng),random.randint(0,rng)))
        row += str(randomDecimal) + " "
    print(row)