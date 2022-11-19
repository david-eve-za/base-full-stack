import {NgModule} from '@angular/core';
import {RouterModule, Routes} from "@angular/router";
import {HomeComponent} from "./components/home/home.component";
import {CategoryComponent} from "../category/components/category/category.component";
import {ProductComponent} from "../product/product/product.component";
import {AuthGuard} from "../guards/auth.guard";

const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'home', component: HomeComponent},
  {path: 'category', component: CategoryComponent,data:{roles:['admin']},canActivate: [AuthGuard]},
  {path: 'product', component: ProductComponent}
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class RouterChildModule {
}
