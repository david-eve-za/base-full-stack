import {Component, OnInit} from '@angular/core';
import {ICardUser} from "@shared/components/cards/card-user/icard-user.metadata";

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.css']
})
export class UserListComponent implements OnInit {

  public users: ICardUser[] = [
    {
      avatar: 'https://randomuser.me/api/portraits/men/75.jpg',
      name: 'John Doe',
      age: 30,
      description: 'Lorem ipsum dolor sit amet'
    },{
      avatar: 'https://randomuser.me/api/portraits/men/75.jpg',
      name: 'John Doe',
      age: 30,
      description: 'Lorem ipsum dolor sit amet'
    },{
      avatar: 'https://randomuser.me/api/portraits/men/75.jpg',
      name: 'John Doe',
      age: 30,
      description: 'Lorem ipsum dolor sit amet'
    },{
      avatar: 'https://randomuser.me/api/portraits/men/75.jpg',
      name: 'John Doe',
      age: 30,
      description: 'Lorem ipsum dolor sit amet'
    },{
      avatar: 'https://randomuser.me/api/portraits/men/75.jpg',
      name: 'John Doe',
      age: 30,
      description: 'Lorem ipsum dolor sit amet'
    },{
      avatar: 'https://randomuser.me/api/portraits/men/75.jpg',
      name: 'John Doe',
      age: 30,
      description: 'Lorem ipsum dolor sit amet'
    },{
      avatar: 'https://randomuser.me/api/portraits/men/75.jpg',
      name: 'John Doe',
      age: 30,
      description: 'Lorem ipsum dolor sit amet'
    },{
      avatar: 'https://randomuser.me/api/portraits/men/75.jpg',
      name: 'John Doe',
      age: 30,
      description: 'Lorem ipsum dolor sit amet'
    },{
      avatar: 'https://randomuser.me/api/portraits/men/75.jpg',
      name: 'John Doe',
      age: 30,
      description: 'Lorem ipsum dolor sit amet'
    },{
      avatar: 'https://randomuser.me/api/portraits/men/75.jpg',
      name: 'John Doe',
      age: 30,
      description: 'Lorem ipsum dolor sit amet'
    },{
      avatar: 'https://randomuser.me/api/portraits/men/75.jpg',
      name: 'John Doe',
      age: 30,
      description: 'Lorem ipsum dolor sit amet'
    },{
      avatar: 'https://randomuser.me/api/portraits/men/75.jpg',
      name: 'John Doe',
      age: 30,
      description: 'Lorem ipsum dolor sit amet'
    },{
      avatar: 'https://randomuser.me/api/portraits/men/75.jpg',
      name: 'John Doe',
      age: 30,
      description: 'Lorem ipsum dolor sit amet'
    },{
      avatar: 'https://randomuser.me/api/portraits/men/75.jpg',
      name: 'John Doe',
      age: 30,
      description: 'Lorem ipsum dolor sit amet'
    },{
      avatar: 'https://randomuser.me/api/portraits/men/75.jpg',
      name: 'John Doe',
      age: 30,
      description: 'Lorem ipsum dolor sit amet'
    },{
      avatar: 'https://randomuser.me/api/portraits/men/75.jpg',
      name: 'John Doe',
      age: 30,
      description: 'Lorem ipsum dolor sit amet'
    },{
      avatar: 'https://randomuser.me/api/portraits/men/75.jpg',
      name: 'John Doe',
      age: 30,
      description: 'Lorem ipsum dolor sit amet'
    },{
      avatar: 'https://randomuser.me/api/portraits/men/75.jpg',
      name: 'John Doe',
      age: 30,
      description: 'Lorem ipsum dolor sit amet'
    },{
      avatar: 'https://randomuser.me/api/portraits/men/75.jpg',
      name: 'John Doe',
      age: 30,
      description: 'Lorem ipsum dolor sit amet'
    },{
      avatar: 'https://randomuser.me/api/portraits/men/75.jpg',
      name: 'John Doe',
      age: 30,
      description: 'Lorem ipsum dolor sit amet'
    },{
      avatar: 'https://randomuser.me/api/portraits/men/75.jpg',
      name: 'John Doe',
      age: 30,
      description: 'Lorem ipsum dolor sit amet'
    },{
      avatar: 'https://randomuser.me/api/portraits/men/75.jpg',
      name: 'John Doe',
      age: 30,
      description: 'Lorem ipsum dolor sit amet'
    },{
      avatar: 'https://randomuser.me/api/portraits/men/75.jpg',
      name: 'John Doe',
      age: 30,
      description: 'Lorem ipsum dolor sit amet'
    },{
      avatar: 'https://randomuser.me/api/portraits/men/75.jpg',
      name: 'John Doe',
      age: 30,
      description: 'Lorem ipsum dolor sit amet'
    }
  ];

  constructor() {
  }

  ngOnInit(): void {
  }

}
