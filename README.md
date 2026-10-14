# Compte rendu : Projet eBank, Partie 3

## Frontend Angular

---

## 1. Création du projet Angular

On crée le projet Angular (`ng new <nom-du-projet>`).

![img_52.png](images/img_52.png)

### 1.1 Installation de Bootstrap et Bootstrap Icons

On installe **Bootstrap** :

![img_53.png](images/img_53.png)

On installe **Bootstrap Icons** :

![img_54.png](images/img_54.png)

On fait l'installation via `npm install` :

```bash
npm install bootstrap bootstrap-icons
```

![img_55.png](images/img_55.png)

### 1.2 Import des styles

Puis on importe Bootstrap et Bootstrap Icons dans `src/styles.css` :

```css
/* You can add global styles to this file, and also import other style files */
@import "bootstrap/dist/css/bootstrap.min.css";
@import "bootstrap-icons/font/bootstrap-icons.min.css";
```

### 1.3 Lancement de l'application

On lance l'application avec `ng serve` :

![img_56.png](images/img_56.png)

Voilà l'interface de notre application :

![img_57.png](images/img_57.png)

---

## 2. Template Bootstrap : la barre de navigation (navbar)

On cherche un template Bootstrap simple sur <https://getbootstrap.com/docs/5.0/components/navbar> et voilà le template :

```html
<nav class="navbar navbar-expand-lg navbar-light bg-light">
  <div class="container-fluid">
    <a class="navbar-brand" href="#">Navbar</a>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarSupportedContent">
      <ul class="navbar-nav me-auto mb-2 mb-lg-0">
        <li class="nav-item">
          <a class="nav-link active" aria-current="page" href="#">Home</a>
        </li>
        <li class="nav-item">
          <a class="nav-link" routerLink="/accounts">Accounts</a>
        </li>
        <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
            Dropdown
          </a>
          <ul class="dropdown-menu" aria-labelledby="navbarDropdown">
            <li><a class="dropdown-item" href="#">Action</a></li>
            <li><a class="dropdown-item" href="#">Another action</a></li>
            <li><hr class="dropdown-divider"></li>
            <li><a class="dropdown-item" href="#">Something else here</a></li>
          </ul>
        </li>
        <li class="nav-item">
          <a class="nav-link disabled" href="#" tabindex="-1" aria-disabled="true">Disabled</a>
        </li>
      </ul>
      <form class="d-flex">
        <input class="form-control me-2" type="search" placeholder="Search" aria-label="Search">
        <button class="btn btn-outline-success" type="submit">Search</button>
      </form>
    </div>
  </div>
</nav>

<router-outlet></router-outlet>
```

On le met dans `app.html`.

> Pour que `routerLink` fonctionne, il faut importer `RouterLink` (et `RouterOutlet`) dans le tableau `imports` du composant `App`.

### 2.1 Le menu ne marche pas : import du JavaScript de Bootstrap

Là, le menu ne marche pas : il faut **importer le JavaScript de Bootstrap**. Les styles seuls ne suffisent pas pour la liste déroulante et le bouton du menu.

![img_58.png](images/img_58.png)

On ajoute donc ceci dans `angular.json` (section `build → options`) :

```json
"styles": [
  "src/styles.css"
],
"scripts": [
  "node_modules/bootstrap/dist/js/bootstrap.bundle.js"
]
```

Et voilà : le JS est chargé et la navbar marche bien. On redémarre (`ng serve`) et la navbar s'affiche, avec la liste déroulante :

![img_59.png](images/img_59.png)

---

## 3. Le composant `Accounts`

On crée notre composant web avec :

```bash
ng g c accounts
```

![img_60.png](images/img_60.png)

On l'ajoute dans les routes (`app.routes.ts`) :

```ts
{ path: "accounts", component: Accounts },
```

> Les chemins Angular sont **sensibles à la casse** : le `path` doit être `"accounts"` (en minuscules) pour correspondre au `routerLink="/accounts"`.

Et on l'affiche dans la barre de navigation du template :

```html
<a class="nav-link" routerLink="/accounts">Accounts</a>
```

Et voilà, on va voir l'application : ça marche.

![img_61.png](images/img_61.png)

---

## 4. Appel du backend depuis le composant

On passe à démarrer notre backend, puis on écrit dans notre composant :

```ts
import {Component, inject} from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Component({
  selector: 'app-accounts',
  imports: [],
  templateUrl: './accounts.html',
  styleUrl: './accounts.css',
})
export class Accounts {
  private http = inject(HttpClient);
  accounts$ = this.http.get('http://localhost:9999/EBANK-SERVICE/accounts');
}
```

> Les comptes sont gérés par **EBANK-SERVICE** (et non CUSTOMER-SERVICE). Pour que `HttpClient` soit injectable, il faut aussi déclarer `provideHttpClient()` dans `app.config.ts` (voir section 8, car on y ajoutera l'intercepteur).

Avec un **Observable**, on ne peut afficher les données que si on s'y **abonne** (*subscribe*), ou avec le pipe `async` dans le template. On précise aussi le type :

```ts
accounts$ = this.http.get<any[]>('http://localhost:9999/EBANK-SERVICE/accounts');
```

On ajoute ceci dans le template `accounts.html` (il faut aussi importer `AsyncPipe` dans le composant) :

```html
<div class="p-3">
  @if (accounts$ | async; as accountList) {
    <table class="table">
      <thead>
        <tr>
          <th>ID</th>
          <th>Balance</th>
          <th>Date</th>
          <th>Type</th>
          <th>CustomerId</th>
        </tr>
      </thead>

      <tbody>
        @for (acc of accountList; track acc.id) {
          <tr>
            <td>{{ acc.id }}</td>
            <td>{{ acc.balance }}</td>
            <td>{{ acc.createdAt }}</td>
            <td>{{ acc.type }}</td>
            <td>{{ acc.customerId }}</td>
          </tr>
        }
      </tbody>
    </table>
  }
</div>
```

---

## 5. Problème CORS

On teste : ça ne marche pas, car il y a un **problème CORS** (*Cross-Origin Resource Sharing*).

C'est-à-dire que notre backend n'autorise pas un frontend qui vient d'un autre nom de domaine (ici le frontend est sur `localhost:4200`, et la Gateway sur `localhost:9999`).

Ici, on ajoute dans le `application.yml` de la **Gateway** :

```yaml
spring:
  cloud:
    gateway:
      server:
        webflux:
          globalcors:
            cors-configurations:
              '[/**]':
                allowed-origins: "*"
                allowed-methods: "*"
                allowed-headers: "*"
```

> En production, on remplacera `"*"` par l'origine du frontend (par ex. `http://localhost:4200`).

On redémarre la Gateway, et c'est bon : nos comptes s'affichent, ça marche bien.

![img_62.png](images/img_62.png)

---

## 6. Création du modèle

On passe à la création de notre **modèle** avec les deux interfaces `Account` et `AccountList`. Voilà notre modèle :

```ts
export interface Account {
  id?: string,
  balance?: number,
  createdAt?: string,
  type?: string,
  customerId?: number
}

export interface AccountList {
  accounts: Account[]
}
```

![img_63.png](images/img_63.png)

### 6.1 Modèle final avec gestion d'état

On modifie ensuite notre modèle (`account.model.ts`) pour gérer **l'état de la requête** (succès / erreur) : `AccountListState` remplace `AccountList`.

```ts
export interface Account {
  id?: string,
  balance?: number,
  createdAt?: string,
  type?: string,
  customerId?: number
}

export enum RequestStatus {
  SUCCESS = 0,
  ERROR = 1
}

export interface AccountListState {
  accounts?: Account[],
  status?: RequestStatus,
  message?: string
}
```

### 6.2 Composant avec `catchError`

Pour **attraper les erreurs**, on utilise les opérateurs RxJS `map` et `catchError` :

```ts
import {Component, inject} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {AsyncPipe} from '@angular/common';
import {Account, AccountListState, RequestStatus} from '../model/account.model';
import {catchError, map, Observable, of} from 'rxjs';

@Component({
  selector: 'app-accounts',
  imports: [
    AsyncPipe
  ],
  templateUrl: './accounts.html',
  styleUrl: './accounts.css',
})
export class Accounts {
  private http = inject(HttpClient);

  // Permet d'utiliser l'enum dans le template
  protected readonly RequestStatus = RequestStatus;

  accounts$: Observable<AccountListState> = this.http
    .get<Account[]>('http://localhost:9999/EBANK-SERVICE/accounts')
    .pipe(
      map(resp => ({ accounts: resp, status: RequestStatus.SUCCESS })),
      catchError(err => of({ status: RequestStatus.ERROR, message: err.statusText }))
    );
}
```

Et dans le template :

```html
<div class="p-3">
  @if (accounts$ | async; as state) {
    <table class="table">
      <thead>
        <tr>
          <th>ID</th>
          <th>Balance</th>
          <th>Date</th>
          <th>Type</th>
          <th>CustomerId</th>
        </tr>
      </thead>

      <tbody>
        @for (acc of state.accounts; track acc.id) {
          <tr>
            <td>{{ acc.id }}</td>
            <td>{{ acc.balance }}</td>
            <td>{{ acc.createdAt }}</td>
            <td>{{ acc.type }}</td>
            <td>{{ acc.customerId }}</td>
          </tr>
        }
      </tbody>
    </table>
  }
</div>
```

On démarre, et c'est bon : toute la liste des comptes s'affiche.

![img_64.png](images/img_64.png)

---

## 7. Affichage des erreurs

Comme on a un **état** (`state`), on peut faire :

```html
@if (state.status == RequestStatus.ERROR) {
  <div class="text-danger">
    {{ state.message }}
  </div>
} @else if (state.status == RequestStatus.SUCCESS) {
  <!-- le tableau des comptes -->
}
```

Ainsi, s'il y a une erreur, on affiche le **message d'erreur**. On met notre tableau dans le cas succès, et le `div` d'erreur dans le cas erreur.

On essaie de **simuler une erreur** avec un endpoint erroné :

```ts
'http://localhost:9999/EBANK-SERVICE/act'
```

Et voilà, « Not Found » s'affiche dans le navigateur. On corrige donc notre endpoint (retour à `/accounts`).

![img_65.png](images/img_65.png)

---

## 8. Affichage « loading » (requête en cours) avec un intercepteur

On passe à l'affichage d'un indicateur **« in progress »** pendant que la requête est en cours.

Pour cela, on passe par un **intercepteur HTTP**, et on crée d'abord un service :

```bash
ng g s services/loading
```

On utilise un **signal** de type booléen, avec la valeur par défaut `false`, et on crée une méthode pour modifier le chargement (`setLoading`).

![img_66.png](images/img_66.png)

### 8.1 Le service `LoadingService`

Première version (le nom de la méthode ne correspondait pas à ce qu'elle faisait : `setLoaded()` mettait toujours à `true`) :

```ts
loading = signal<boolean>(false);
public isLoading = this.loading.asReadonly();
public setLoaded() {
  this.loading.set(true);
}
```

On la modifie pour passer la valeur en paramètre :

```ts
import {Injectable, signal} from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class LoadingService {
  loading = signal<boolean>(false);
  public isLoading = this.loading.asReadonly();

  public setLoading(value: boolean) {
    this.loading.set(value);
  }
}
```

> On expose `isLoading` en **public** pour pouvoir l'appeler depuis le template (un membre `private` n'est pas accessible dans le template ; `protected` l'est dans le composant lui-même).

### 8.2 L'intercepteur `loading`

On crée aussi notre intercepteur (`ng g interceptor interceptors/loading`). À chaque fois que j'envoie une requête, elle passe par cet intercepteur. On y injecte notre `LoadingService` :

```ts
const loadingService = inject(LoadingService);
```

Au moment où la requête part, on passe le chargement à `true`. Une fois la réponse arrivée, on laisse passer la réponse et on utilise l'opérateur `finalize` pour repasser à `false` :

```ts
import {HttpInterceptorFn} from '@angular/common/http';
import {inject} from '@angular/core';
import {finalize} from 'rxjs';
import {LoadingService} from '../services/loading';

export const loadingInterceptor: HttpInterceptorFn = (req, next) => {
  const loadingService = inject(LoadingService);

  loadingService.setLoading(true);

  return next(req).pipe(
    finalize(() => {
      loadingService.setLoading(false);
    })
  );
};
```

On a ainsi un intercepteur qui fait ce travail **une fois pour toute l'application**.

> Remarque : avec un simple booléen, si plusieurs requêtes sont lancées en parallèle, le spinner disparaît dès que la première se termine. Pour un cas plus robuste, on utilise un **compteur** de requêtes en cours.

### 8.3 Enregistrement de l'intercepteur

On l'injecte dans notre application (`app.config.ts`) :

```ts
import {ApplicationConfig} from '@angular/core';
import {provideRouter} from '@angular/router';
import {provideHttpClient, withInterceptors} from '@angular/common/http';
import {routes} from './app.routes';
import {loadingInterceptor} from './interceptors/loading-interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptors([loadingInterceptor]))
  ]
};
```

(Le nom du fichier de l'intercepteur dépend de ce que génère `ng g interceptor`.)

### 8.4 Le spinner dans le composant

Dans le composant `Accounts`, on injecte le service :

```ts
public loadingService = inject(LoadingService);
```

Puis on ajoute le spinner dans notre `div`, pour le chargement des données :

```html
@if (loadingService.isLoading()) {
  <div class="spinner-border text-primary" role="status">
    <span class="visually-hidden">Loading ...</span>
  </div>
}
```

Résultats :

![img_67.png](images/img_67.png)

![img_68.png](images/img_68.png)

![img_69.png](images/img_69.png)

![img_70.png](images/img_70.png)

---

## 9. Récapitulatif des corrections apportées

| # | Endroit | Erreur dans la version d'origine | Correction |
|---|---|---|---|
| 1 | Image `img_52` | Crochets parasites autour du lien image | Lien image corrigé |
| 2 | `angular.json` | Instructions peu claires sur l'emplacement | Précisé : `build → options` (`styles` + `scripts`) |
| 3 | Navbar | `RouterLink` / `RouterOutlet` à importer non mentionnés | Remarque ajoutée |
| 4 | Commande | `ng g c accoutns` (faute de frappe) | `ng g c accounts` |
| 5 | Route | `path: "Accounts"` alors que `routerLink="/accounts"` (Angular est sensible à la casse) | `path: "accounts"` |
| 6 | Premier appel HTTP | URL `CUSTOMER-SERVICE/accounts` alors que les comptes sont dans EBANK-SERVICE | `EBANK-SERVICE/accounts` |
| 7 | `app.config.ts` | `provideHttpClient()` non mentionné (HttpClient non injectable) | Ajouté (avec l'intercepteur) |
| 8 | Composant | `AsyncPipe` non importé dans la première version | Ajouté à `imports` |
| 9 | Gateway YAML (CORS) | Indentation cassée | YAML valide ; remarque sur `"*"` en production |
| 10 | Appel HTTP | `get<Observable<AccountListState>>(...)` (mauvais type : la réponse est un tableau de comptes) | `get<Account[]>(...)` + `map` vers `AccountListState` |
| 11 | `catchError` | `errorMessage` alors que l'interface déclare `message` ; paramètre `caught` inutile | `message: err.statusText`, paramètre retiré |
| 12 | Template | `RequestStatus` utilisé dans le template sans être exposé par le composant | `protected readonly RequestStatus = RequestStatus;` |
| 13 | Template | `track acc` (suit l'objet entier) | `track acc.id` |
| 14 | `LoadingService` | `setLoaded()` mettait toujours `true` (nom trompeur) | `setLoading(value: boolean)` |
| 15 | Intercepteur | Enregistrement dans `app.config.ts` non montré | `provideHttpClient(withInterceptors([...]))` |
| 16 | Spinner | Accès à `isLoading` impossible depuis le template (membre non public) | Service injecté en `public` dans le composant |
| 17 | Spinner | Booléen unique fragile avec plusieurs requêtes simultanées | Remarque ajoutée (compteur de requêtes) |
| 18 | Orthographe / formulation | Nombreuses fautes de frappe (« Bootstrat », « intecepetoer », « cad »…) | Texte reformulé et corrigé |
