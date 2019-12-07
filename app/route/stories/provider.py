from app.api.base.base_sql import Sql


class Provider:
    @staticmethod
    def publicate_storie(args):
        query = """
  insert into "publicated_stories" ("id_stories", "id_user") 
  VALUES ({id_stories}, {id_user})
  """
        return Sql.exec(query=query, args=args)

    @staticmethod
    def insert_stories(args):
        query = """
  insert into stories ("id_user", "id_creator")
  VALUES ({id_user}, {id_user})
  returning "id_stories"
  """
        return Sql.exec(query=query, args=args)

    @staticmethod
    def stories_profile(args):
        query = """
  select stories.* from users 
  join stories on stories."id_user" = users."id_user" and stories."type" = {type}
  where "id_profile" = {id_profile}
  """
        return Sql.exec(query=query, args=args)

    @staticmethod
    def delete_images(args):
        query = """
  delete from images where "id_stories" = {id_stories}
  """
        return Sql.exec(query=query, args=args)

    @staticmethod
    def insert_image(args):
        query = """
  insert into images ("id_stories", "url", "position")
  VALUES ({id_stories}, '{url}', {position})
  """
        return Sql.exec(query=query, args=args)

    @staticmethod
    def select_status(args):
        query = """
  select 
    True
  from step_action 
  where "id_user" = {id_user} and "id_stories" = {id_stories}
  """
        return Sql.exec(query=query, args=args)

    @staticmethod
    def insert_status(args):
        query = """
  insert into step_action("id_stories", "id_user", "is_open", "is_view", "time", "is_like") 
  VALUES ({id_stories}, {id_user}, {is_open}, {is_view}, NOW(), {is_like}::boolean)
  """
        return Sql.exec(query=query, args=args)

    @staticmethod
    def update_status(args):
        query = """
  update step_action
    set "is_open" = {is_open}, 
        "is_view" = {is_view},
        "time" = NOW()
    where "id_user" = {id_user} and "id_stories" = {id_stories}
  """
        return Sql.exec(query=query, args=args)

    @staticmethod
    def update_like(args):
        query = """
  update step_action
    set "is_like" = {is_like}::boolean
    where "id_user" = {id_user} and "id_stories" = {id_stories}
  """
        return Sql.exec(query=query, args=args)

    @staticmethod
    def update_notifications_user(args):
        query = """
  update notifications_users
    set "active" = {active}, 
        "time" = NOW() + interval '5 minutes'
    where "id_notification" = {id_notification}
  """
        return Sql.exec(query=query, args=args)

    @staticmethod
    def update_stories(args):
        query = """
  update stories
    set "id_creator" = {id_user}
    where "id_stories" = {id_stories}
  """
        return Sql.exec(query=query, args=args)

    @staticmethod
    def insert_notifications_users(args):
        query = """
  insert into notifications_users ("id_notification", "id_user", "status") 
  VALUES ('{id_notification}', '{id_user}', '{status}')
  """
        return Sql.exec(query=query, args=args)

    @staticmethod
    def get_notifications(args):
        query = """
  select notifications.* from notifications_users
  join notifications on notifications."id_notification" = notifications_users."id_notification"
  where "id_user" = {id_user}
  """
        return Sql.exec(query=query, args=args)

    @staticmethod
    def update_profile(args):
        query = """
  update profile 
    set "description" = '{description}'
    where "id_profile" = {id_profile}
  """
        return Sql.exec(query=query, args=args)

    @staticmethod
    def get_stories_list(args):
        query = """
  select
    img."id_stories",
    array_agg(img."url" order by position) as image
  from images img
  join stories str on img."id_stories" = str."id_stories" and str."type" = {type}
  join publicated_stories ps on ps."id_stories" = str."id_stories"
  where str."id_user" = {id_user}
  group by img."id_stories"
  """
        return Sql.exec(query=query, args=args)

    @staticmethod
    def get_all_stories(args):
        query = """
  with stories_all as(
    select 
      img."id_stories",
      array_agg(img."url" order by position desc) as image
    from images img
    join stories str on img."id_stories" = str."id_stories" and str."type" = {type}
    group by img."id_stories"
  ),
  public_open as (
    select
      distinct 
      ps."id_stories",
      pr."id_profile",
      pr."description"
    from publicated_stories ps
    join users u on ps."id_user" = u."id_user"
    join profile pr on u."id_profile" = pr."id_profile"
  )
  select 
    sa.*
    , array_agg(json_build_object('id_profile',pr."id_profile", 'name', pr."description") order by pr."description") open
  from stories_all sa
  join public_open pr using("id_stories")
  group by sa."id_stories", image
  """
        return Sql.exec(query=query, args=args)
