import { NgModule } from '@angular/core';
import {RouterModule, Routes} from "@angular/router";
import {UserListComponent} from "@modules/user/user-list/user-list.component";
import {UserDetailComponent} from "@modules/user/user-detail/user-detail.component";


const routes: Routes = [
  {
    path: '',
    component: UserListComponent
  },
  {
    path: ':id',
    component: UserDetailComponent
  }
  ];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UserRoutingModule { }
