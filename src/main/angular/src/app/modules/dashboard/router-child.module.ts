import {NgModule} from '@angular/core';
import {RouterModule, Routes} from "@angular/router";
import {HomeComponent} from "./components/home/home.component";
import {CategoryComponent} from "../category/components/category/category.component";
import {ProductComponent} from "../product/product/product.component";
import {AuthGuard} from "../guards/auth.guard";
import {BooksComponent} from "../books/components/books/books.component";
import {ChaptersComponent} from "../books/components/chapters/chapters.component";

const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'home', component: HomeComponent},
  {path: 'category', component: CategoryComponent, data: {roles: ['admin']}, canActivate: [AuthGuard]},
  {path: 'product', component: ProductComponent},
  {
    path: 'books',
    component: BooksComponent,
    data: {roles: ['admin']},
    canActivate: [AuthGuard],
    canActivateChild: [AuthGuard],
    children: [
      {path: 'chapters', component: ChaptersComponent},
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class RouterChildModule {
}
