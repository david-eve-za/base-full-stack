import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {ICardUser} from "@shared/components/cards/card-user/icard-user.metadata";
import {USERS_DATA} from "@data/constants/users.const";

@Component({
  selector: 'app-user-detail',
  templateUrl: './user-detail.component.html',
  styleUrls: ['./user-detail.component.css']
})
export class UserDetailComponent implements OnInit {
  public users: ICardUser[] = USERS_DATA;
  public id: number;
  public currentUser: ICardUser | undefined;

  constructor(private route: ActivatedRoute) {
    this.id = +this.route.snapshot.params['id'];
    this.currentUser = this.users.find(user => user.id === this.id);
    console.log(this.currentUser);
  }

  ngOnInit(): void {
  }

}
