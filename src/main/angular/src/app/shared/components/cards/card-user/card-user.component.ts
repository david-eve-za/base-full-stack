import {Component, Input, OnInit} from '@angular/core';
import {ICardUser} from "@shared/components/cards/card-user/icard-user.metadata";

@Component({
  selector: 'app-card-user',
  templateUrl: './card-user.component.html',
  styleUrls: ['./card-user.component.css']
})
export class CardUserComponent implements OnInit {

  @Input() data: ICardUser | undefined;

  constructor() { }

  ngOnInit(): void {
  }

}
