def remove_repeticoes(lista):
    lista_sem_repeticao = []
    for item in lista:
        if item not in lista_sem_repeticao:
            lista_sem_repeticao.append(item)
    return lista_sem_repeticao


def breakingRecords(scores: list):
    count_min = []
    count_max = []
    lista = remove_repeticoes(scores)
    cache = []
    for i, e in enumerate(lista):
        cache.append(e)
        if i >= 1:
            if  e > lista[i-1]:
                if len(count_max) == 0 and e >= max(cache):
                    count_max.append(e)
                
                if len(count_max) > 0 and e > max(count_max): 
                    count_max.append(e)
            else:
                if e <= min(cache):
                    count_min.append(e)
    return len(count_max), len(count_min)
          
print(breakingRecords([100, 45, 41, 60, 17, 41, 45, 43, 100, 40, 89, 92, 34, 6, 64, 7, 37, 81, 32, 50]))
print(breakingRecords([10, 5, 20, 20, 4, 5, 2, 25, 1]))